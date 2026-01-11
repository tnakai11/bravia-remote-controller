package com.example.braviaremotecontroler.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Sony Bravia TVと通信するためのRetrofitインターフェースです。
 * IRCC (Infrared Remote Control Command) や JSON-RPC を使用してテレビを制御します。
 */
interface BraviaApi {

    /**
     * IRCCコマンド（リモコンキー信号）をテレビに送信します。
     *
     * @param psk 事前共有鍵 (Pre-Shared Key)。テレビの設定で構成されたもの。
     * @param body 送信するIRCCコマンドを含むXML形式のRequestBody。
     * @return APIレスポンス。
     */
    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPACTION: \"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\""
    )
    @POST("sony/ircc")
    suspend fun sendIrcc(
        @Header("X-Auth-PSK") psk: String,
        @Body body: RequestBody
    ): Response<Unit>

    /**
     * システム関連のJSON-RPCリクエストを送信します。
     *
     * @param psk 事前共有鍵。
     * @param body JSON-RPCリクエストオブジェクト。
     * @return JSON-RPCレスポンス。
     */
    @POST("sony/system")
    suspend fun system(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>

    /**
     * オーディオ関連（音量など）のJSON-RPCリクエストを送信します。
     *
     * @param psk 事前共有鍵。
     * @param body JSON-RPCリクエストオブジェクト。
     * @return JSON-RPCレスポンス。
     */
    @POST("sony/audio")
    suspend fun audio(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>

    /**
     * アプリ制御関連のJSON-RPCリクエストを送信します。
     *
     * @param psk 事前共有鍵。
     * @param body JSON-RPCリクエストオブジェクト。
     * @return JSON-RPCレスポンス。
     */
    @POST("sony/appControl")
    suspend fun appControl(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>
}

/**
 * Bravia API への JSON-RPC リクエストのデータ構造。
 *
 * @property method 実行するメソッド名。
 * @property id リクエストを識別するID。通常は1で固定。
 * @property params メソッドに渡すパラメータ。
 * @property version JSON-RPCのバージョン。通常は"1.0"。
 */
data class JsonRpcRequest(
    val method: String,
    val id: Int = 1,
    val params: List<Any> = emptyList(),
    val version: String = "1.0"
)

/**
 * Bravia API からの JSON-RPC レスポンスのデータ構造。
 *
 * @property id リクエストに対応するID。
 * @property result 成功時の結果データ。
 * @property error エラー発生時のエラー情報。
 */
data class JsonRpcResponse(
    val id: Int,
    val result: List<Any>?,
    val error: List<Any>?
)
