package com.example.braviaremotecontroler.ui.debug

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.braviaremotecontroler.api.BraviaRemoteManager
import com.example.braviaremotecontroler.data.BraviaSettings
import kotlinx.coroutines.launch

class DebugRemoteViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = BraviaSettings(application)
    private var remoteManager: BraviaRemoteManager? = null

    private val _commands = MutableLiveData<Map<String, String>>()
    val commands: LiveData<Map<String, String>> = _commands

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorEvent = MutableLiveData<String?>()
    val errorEvent: LiveData<String?> = _errorEvent

    init {
        updateRemoteManager()
    }

    private fun updateRemoteManager() {
        val ip = settings.ipAddress
        val psk = settings.psk
        remoteManager = if (ip.isNotBlank() && psk.isNotBlank()) {
            BraviaRemoteManager(ip, psk)
        } else {
            null
        }
    }

    fun loadCommands() {
        val manager = remoteManager ?: run {
            _errorEvent.value = "IPアドレスとPSKを設定してください"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val info = manager.loadRemoteControllerInfo()
                if (info.isEmpty()) {
                    _errorEvent.value = "コマンドを取得できませんでした"
                }
                _commands.value = info
            } catch (e: Exception) {
                _errorEvent.value = "エラー: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun sendCommand(code: String) {
        val manager = remoteManager ?: return
        viewModelScope.launch {
            try {
                manager.sendRawIrcc(code)
            } catch (e: Exception) {
                _errorEvent.value = "送信エラー: ${e.message}"
            }
        }
    }

    fun clearError() {
        _errorEvent.value = null
    }
}
