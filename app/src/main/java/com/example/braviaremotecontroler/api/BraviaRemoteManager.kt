package com.example.braviaremotecontroler.api

import android.util.Log

/**
 * Sony Bravia TVのリモコン操作を抽象化して管理するクラス。
 * 固定のIRCCキーコードの送信や、動的に取得したコマンド名による送信などを提供します。
 *
 * @param ipAddress テレビのIPアドレス。
 * @param psk 事前共有鍵。
 */
class BraviaRemoteManager(ipAddress: String, psk: String) {

    companion object {
        private const val TAG = "BraviaRemoteManager"
    }

    private val client = BraviaClient(ipAddress, psk)
    private var irccCache: Map<String, String>? = null

    /**
     * テレビとの接続テストを行います。
     * 電源状態を取得するリクエストを送信し、有効なレスポンスが返ってくるか確認します。
     *
     * @return 接続に成功した場合はtrue、失敗した場合はfalse。
     */
    suspend fun testConnection(): Boolean {
        return try {
            val response = client.jsonRpc("system", "getPowerStatus")
            response != null && response.error == null
        } catch (e: Exception) {
            false
        }
    }

    /**
     * テレビから利用可能なリモコンコマンド（IRCC）のリストを取得し、キャッシュします。
     *
     * @return コマンド名をキー、IRCCコードを値とするマップ。
     */
    suspend fun loadRemoteControllerInfo(): Map<String, String> {
        irccCache?.let { return it }

        return try {
            val response = client.jsonRpc("system", "getRemoteControllerInfo")
            val resultList = response?.result?.firstOrNull { it is List<*> } as? List<*>
            val map: Map<String, String> = resultList
                ?.mapNotNull { item ->
                    (item as? Map<*, *>)?.let {
                        val name = it["name"] as? String
                        val value = it["value"] as? String
                        if (name != null && value != null) name to value else null
                    }
                }
                ?.toMap()
                ?: emptyMap()

            if (map.isNotEmpty()) {
                Log.d(TAG, "Loaded IRCC codes: ${map.keys}")
                irccCache = map
            }
            map
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load remote controller info", e)
            emptyMap()
        }
    }

    /**
     * 複数の候補名から、テレビがサポートしている最初のIRCCコードを解決します。
     *
     * @param candidates 検索するコマンド名のリスト。
     * @return 見つかった場合はIRCCコード、見つからない場合はnull。
     */
    private suspend fun resolveIrccByNames(candidates: List<String>): String? {
        val cache = loadRemoteControllerInfo()
        return candidates.firstNotNullOfOrNull { cache[it] }
    }

    /**
     * 指定された [IrccKey] に対応するコマンドをテレビに送信します。
     *
     * @param key 送信するキーの列挙型。
     */
    suspend fun send(key: IrccKey) = client.sendIrcc(key.code)

    /**
     * 指定されたIRCCコードをテレビに送信します。
     *
     * @param code IRCCコード。
     */
    suspend fun sendRawIrcc(code: String) = client.sendIrcc(code)

    /** 電源ボタンを押します。 */
    suspend fun power() = send(IrccKey.Power)
    /** 入力切換ボタンを押します。 */
    suspend fun input() = send(IrccKey.Input)

    /** 音量を上げます。 */
    suspend fun volumeUp() = send(IrccKey.VolumeUp)
    /** 音量を下げます。 */
    suspend fun volumeDown() = send(IrccKey.VolumeDown)
    /** 消音を切り替えます。 */
    suspend fun mute() = send(IrccKey.Mute)

    /** チャンネルを上げます。 */
    suspend fun channelUp() = send(IrccKey.ChannelUp)
    /** チャンネルを下げます。 */
    suspend fun channelDown() = send(IrccKey.ChannelDown)

    /** 上ボタンを押します。 */
    suspend fun up() = send(IrccKey.Up)
    /** 下ボタンを押します。 */
    suspend fun down() = send(IrccKey.Down)
    /** 左ボタンを押します。 */
    suspend fun left() = send(IrccKey.Left)
    /** 右ボタンを押します。 */
    suspend fun right() = send(IrccKey.Right)
    /** 決定ボタンを押します。 */
    suspend fun confirm() = send(IrccKey.Confirm)
    /** ホームボタンを押します。 */
    suspend fun home() = send(IrccKey.Home)
    /** 戻るボタンを押します。 */
    suspend fun back() = send(IrccKey.Back)

    /** YouTubeを起動しようとします。 */
    suspend fun youtube() = sendByName(listOf("YouTube", "Youtube", "YT", "APPS_YOUTUBE"))
    /** Netflixを起動しようとします。 */
    suspend fun netflix() = sendByName(listOf("Netflix", "NETFLIX", "APPS_NETFLIX"))
    /** TVモードに切り替えようとします。 */
    suspend fun tv() = sendByName(listOf("Tv", "TV", "TvAnalog", "TvAntenna"))
    /** デモモードに切り替えようとします。 */
    suspend fun demoMode() = sendByName(listOf("DemoMode", "Demo Mode", "DEMO_MODE", "Demo"))

    /**
     * 候補名のリストから最初に見つかったコマンドを送信します。
     *
     * @param candidates コマンド名の候補。
     */
    private suspend fun sendByName(candidates: List<String>) {
        val code = resolveIrccByNames(candidates)
        if (code != null) {
            client.sendIrcc(code)
        } else {
            Log.e(TAG, "Command not found for candidates: $candidates")
        }
    }
}
