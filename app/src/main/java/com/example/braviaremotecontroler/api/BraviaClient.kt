package com.example.braviaremotecontroler.api

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Sony Bravia TVとの通信を低レベルで管理するクライアントクラス。
 */
class BraviaClient(ipAddress: String, private val psk: String) {

    companion object {
        private const val IRCC_XML_TEMPLATE = """
            <?xml version="1.0" encoding="utf-8"?>
            <s:Envelope xmlns:s="http://schemas.xmlsoap.org/soap/envelope/" s:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
                <s:Body>
                    <u:X_SendIRCC xmlns:u="urn:schemas-sony-com:service:IRCC:1">
                        <IRCCCode>%s</IRCCCode>
                    </u:X_SendIRCC>
                </s:Body>
            </s:Envelope>
        """
        private val MEDIA_TYPE_XML = "text/xml; charset=utf-8".toMediaType()
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("http://$ipAddress/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val api = retrofit.create(BraviaApi::class.java)

    /**
     * 指定されたIRCCコマンドコードをテレビに送信します。
     */
    suspend fun sendIrcc(commandCode: String) {
        val xmlBody = String.format(IRCC_XML_TEMPLATE.trimIndent(), commandCode)
        val requestBody = xmlBody.toRequestBody(MEDIA_TYPE_XML)
        api.sendIrcc(psk, requestBody)
    }

    /**
     * JSON-RPCリクエストを送信します。
     */
    suspend fun jsonRpc(service: String, method: String, params: List<Any> = emptyList()): JsonRpcResponse? {
        val request = JsonRpcRequest(method = method, params = params)
        val response = try {
            when (service) {
                "system" -> api.system(psk, request)
                "audio" -> api.audio(psk, request)
                "appControl" -> api.appControl(psk, request)
                else -> throw IllegalArgumentException("Unknown service: $service")
            }
        } catch (_: Exception) {
            return null
        }
        return if (response.isSuccessful) response.body() else null
    }
}
