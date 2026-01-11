package com.example.braviaremotecontroler.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

/**
 * アプリの設定（テレビのIPアドレスや事前共有鍵）を永続化するためのクラス。
 * SharedPreferencesを使用してデータを保存・読み取りします。
 *
 * @param context コンテキスト。
 */
class BraviaSettings(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("bravia_prefs", Context.MODE_PRIVATE)

    /**
     * テレビのIPアドレス。
     */
    var ipAddress: String
        get() = prefs.getString("ip_address", "") ?: ""
        set(value) = prefs.edit { putString("ip_address", value) }

    /**
     * テレビに接続するための事前共有鍵 (PSK)。
     */
    var psk: String
        get() = prefs.getString("psk", "") ?: ""
        set(value) = prefs.edit { putString("psk", value) }
}
