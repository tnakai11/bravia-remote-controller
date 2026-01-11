package com.example.braviaremotecontroler.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.braviaremotecontroler.api.BraviaRemoteManager
import com.example.braviaremotecontroler.data.BraviaSettings
import kotlinx.coroutines.launch

/**
 * 設定画面のロジックとデータを管理するViewModel。
 * IPアドレスやPSK（事前共有鍵）の保存、および接続テストの実行を担当します。
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = BraviaSettings(application)

    private val _ipAddress = MutableLiveData<String>().apply { value = settings.ipAddress }
    /** 現在設定されているIPアドレスのLiveData */
    val ipAddress: LiveData<String> = _ipAddress

    private val _psk = MutableLiveData<String>().apply { value = settings.psk }
    /** 現在設定されているPSK（事前共有鍵）のLiveData */
    val psk: LiveData<String> = _psk

    private val _isTesting = MutableLiveData<Boolean>(false)
    /** 接続テストが実行中かどうかを示すLiveData */
    val isTesting: LiveData<Boolean> = _isTesting

    private val _testResult = MutableLiveData<Boolean?>()
    /** 接続テストの結果を示すLiveData（成功ならtrue、失敗ならfalse、未実行ならnull） */
    val testResult: LiveData<Boolean?> = _testResult

    /**
     * 新しい設定を保存します。
     *
     * @param ip テレビのIPアドレス。
     * @param psk 事前共有鍵。
     * @return 保存に成功した場合はtrue、入力が不完全な場合はfalse。
     */
    fun saveSettings(ip: String, psk: String): Boolean {
        if (ip.isNotEmpty() && psk.isNotEmpty()) {
            settings.ipAddress = ip
            settings.psk = psk
            _ipAddress.value = ip
            _psk.value = psk
            return true
        }
        return false
    }

    /**
     * 指定された設定を使用してテレビとの接続テストを非同期で実行します。
     *
     * @param ip テストするIPアドレス。
     * @param psk テストする事前共有鍵。
     */
    fun testConnection(ip: String, psk: String) {
        viewModelScope.launch {
            _isTesting.value = true
            _testResult.value = null
            
            val testManager = BraviaRemoteManager(ip, psk)
            val success = testManager.testConnection()
            
            _testResult.value = success
            _isTesting.value = false
        }
    }
}
