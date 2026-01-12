package com.example.braviaremotecontroler.api

import android.util.Log
import kotlinx.coroutines.delay

/**
 * Sony Bravia TVのリモコン操作を抽象化して管理するクラス。
 */
class BraviaRemoteManager(ipAddress: String, psk: String) {

    companion object {
        private const val TAG = "BraviaRemoteManager"
        
        /** 
         * YouTube TVアプリのキーボードレイアウト (英字 7列)
         */
        val YOUTUBE_LAYOUT_EN = listOf(
            "ABCDEFG",
            "HIJKLMN",
            "OPQRSTU",
            "VWXYZ12",
            "3456789",
            "0"
        )

        /**
         * YouTube TVアプリの日本語キーボードレイアウト
         * 
         * ユーザーの指摘に基づいたレイアウト構造:
         * - 一番左の列 (Col 0) が「あいうえお」 (垂直方向)
         * - 「やゆよ」は「あいうえお」と同じ行 (Row 0, 2, 4) に配置
         * - 「わをん」も「あいうえお」と同じ行 (Row 0, 2, 4) に配置
         * - 「わ」行の右隣の列に「濁点」「半濁点」「小文字」「ー」が配置
         * 
         * 可視化 (行方向):
         * Row 0: あ か さ た な は ま や ら わ ゛
         * Row 1: い き し ち に ひ み 　 り 　 ゜
         * Row 2: う く す つ ぬ ふ む ゆ る を 小
         * Row 3: え け せ て ね へ め 　 れ 　 ー
         * Row 4: お こ そ と の ほ も よ ろ ん
         */
        val YOUTUBE_LAYOUT_JA = listOf(
            "あかさたなはまやらわ゛",
            "いきしちにひみ　り　゜",
            "うくすつぬふむゆるを小",
            "えけせてねへめ　れ　ー",
            "おこそとのほもよろん　"
        )

        /**
         * 日本語入力時の特殊なマッピング
         */
        val JA_CHAR_MAPPING = mapOf(
            'が' to "か゛", 'ぎ' to "き゛", 'ぐ' to "く゛", 'げ' to "け゛", 'ご' to "こ゛",
            'ざ' to "さ゛", 'じ' to "し゛", 'ず' to "す゛", 'ぜ' to "せ゛", 'ぞ' to "そ゛",
            'だ' to "た゛", 'ぢ' to "ち゛", 'づ' to "つ゛", 'で' to "て゛", 'ど' to "と゛",
            'ば' to "は゛", 'び' to "ひ゛", 'ぶ' to "ふ゛", 'べ' to "へ゛", 'ぼ' to "ほ゛",
            'ぱ' to "は゜", 'ぴ' to "ひ゜", 'ぷ' to "ふ゜", 'ぺ' to "へ゜", 'ぽ' to "ほ゜",
            'ぁ' to "あ小", 'ぃ' to "い小", 'ぅ' to "う小", 'ぇ' to "え小", 'ぉ' to "お小",
            'っ' to "つ小", 'ゃ' to "や小", 'ゅ' to "ゆ小", 'ょ' to "よ小"
        )
    }

    private val client = BraviaClient(ipAddress, psk)
    private var irccCache: Map<String, String>? = null

    suspend fun testConnection(): Boolean {
        return try {
            val response = client.jsonRpc("system", "getPowerStatus")
            response != null && response.error == null
        } catch (_: Exception) {
            false
        }
    }

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
            if (map.isNotEmpty()) irccCache = map
            map
        } catch (_: Exception) {
            emptyMap()
        }
    }

    private suspend fun resolveIrccByNames(candidates: List<String>): String? {
        val cache = loadRemoteControllerInfo()
        return candidates.firstNotNullOfOrNull { cache[it] }
    }

    suspend fun send(key: IrccKey) = client.sendIrcc(key.code)
    suspend fun sendRawIrcc(code: String) = client.sendIrcc(code)

    suspend fun power() = send(IrccKey.Power)
    suspend fun input() = send(IrccKey.Input)
    suspend fun volumeUp() = send(IrccKey.VolumeUp)
    suspend fun volumeDown() = send(IrccKey.VolumeDown)
    suspend fun mute() = send(IrccKey.Mute)
    suspend fun channelUp() = send(IrccKey.ChannelUp)
    suspend fun channelDown() = send(IrccKey.ChannelDown)
    suspend fun up() = send(IrccKey.Up)
    suspend fun down() = send(IrccKey.Down)
    suspend fun left() = send(IrccKey.Left)
    suspend fun right() = send(IrccKey.Right)
    suspend fun confirm() = send(IrccKey.Confirm)
    suspend fun home() = send(IrccKey.Home)
    suspend fun back() = send(IrccKey.Back)

    suspend fun youtube() = sendByName(listOf("YouTube", "Youtube", "YT", "APPS_YOUTUBE"))
    suspend fun netflix() = sendByName(listOf("Netflix", "NETFLIX", "APPS_NETFLIX"))
    suspend fun tv() = sendByName(listOf("Tv", "TV", "TvAnalog", "TvAntenna"))
    suspend fun gguide() = sendByName(listOf("GGuide", "EPG"))
    suspend fun demoMode() = sendByName(listOf("DemoMode", "Demo Mode", "DEMO_MODE", "Demo"))

    private suspend fun sendByName(candidates: List<String>) {
        val code = resolveIrccByNames(candidates)
        if (code != null) client.sendIrcc(code)
    }

    /**
     * テレビのテキストフォームにテキストを入力します。
     */
    suspend fun setTextForm(text: String): Boolean {
        return try {
            val params = listOf(text)
            val response = client.jsonRpc("appControl", "setTextForm", params)
            response != null && response.error == null
        } catch (e: Exception) {
            Log.e(TAG, "Failed to setTextForm", e)
            false
        }
    }

    /**
     * カーソル移動と決定ボタンを使用してテキストを入力します。
     */
    suspend fun typeTextViaCursor(
        text: String,
        delayMs: Long = 100
    ) {
        val isJapanese = text.any { it.code in 0x3040..0x309F || it.code in 0x30A0..0x30FF }
        val layout = if (isJapanese) YOUTUBE_LAYOUT_JA else YOUTUBE_LAYOUT_EN
        
        val charMap = mutableMapOf<Char, Pair<Int, Int>>()
        layout.forEachIndexed { y, row ->
            row.forEachIndexed { x, char ->
                if (char != '　') {
                    charMap[char] = x to y
                }
            }
        }

        var currentX = 0
        var currentY = 0

        // 日本語の場合は展開して処理
        val processedText = if (isJapanese) {
            val sb = StringBuilder()
            text.forEach { c ->
                val expanded = JA_CHAR_MAPPING[c] ?: c.toString()
                sb.append(expanded)
            }
            sb.toString()
        } else {
            text.uppercase()
        }

        for (char in processedText) {
            val target = charMap[char] ?: continue
            val targetX = target.first
            val targetY = target.second

            // 目的地まで1ステップずつ移動（空白を迂回）
            while (currentX != targetX || currentY != targetY) {
                val dx = targetX - currentX
                val dy = targetY - currentY

                // 水平移動を試みる
                if (dx != 0) {
                    val nextX = currentX + if (dx > 0) 1 else -1
                    if (layout[currentY][nextX] != '　') {
                        if (dx > 0) right() else left()
                        currentX = nextX
                    } else {
                        // 行く手に空白がある場合、垂直方向に避ける
                        // 日本語レイアウトでは 0, 2, 4 行目が常に安全
                        if (currentY % 2 != 0) {
                            if (dy < 0) { up(); currentY-- }
                            else if (dy > 0) { down(); currentY++ }
                            else { if (currentY == 1) { up(); currentY = 0 } else { up(); currentY = 2 } }
                        } else {
                            if (currentY > 0) { up(); currentY-- } else { down(); currentY++ }
                        }
                    }
                }
                // 垂直移動を試みる
                else {
                    val nextY = currentY + if (dy > 0) 1 else -1
                    if (layout[nextY][currentX] != '　') {
                        if (dy > 0) down() else up()
                        currentY = nextY
                    } else {
                        // 垂直方向に空白がある場合、水平方向に避ける（「や」「わ」列など）
                        if (currentX > 0) { left(); currentX-- } else { right(); currentX++ }
                    }
                }
                delay(delayMs)
            }

            // 決定
            confirm()
            delay(delayMs + 200)
        }
    }
}
