package com.example.braviaremotecontroler.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.braviaremotecontroler.api.BraviaRemoteManager
import com.example.braviaremotecontroler.data.BraviaSettings
import kotlinx.coroutines.launch

/**
 * リモコン画面のロジックを管理するViewModel。
 */
class RemoteControlViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = BraviaSettings(application)
    private var remoteManager: BraviaRemoteManager? = null

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent

    init {
        updateRemoteManager()
    }

    /**
     * 保存されている設定に基づいてリモコンマネージャーを更新します。
     */
    fun updateRemoteManager() {
        val ip = settings.ipAddress
        val psk = settings.psk
        
        remoteManager = if (ip.isNotBlank() && psk.isNotBlank()) {
            BraviaRemoteManager(ip, psk)
        } else {
            null
        }
    }

    /**
     * テレビにコマンドを送信します。
     */
    fun sendCommand(action: suspend (BraviaRemoteManager) -> Unit) {
        val manager = remoteManager ?: run {
            _errorEvent.value = "設定画面でIPアドレスとPSKを設定してください"
            return
        }

        viewModelScope.launch {
            runCatching {
                action(manager)
            }.onFailure {
                _errorEvent.value = "通信エラーが発生しました: ${it.message}"
            }
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }
}
