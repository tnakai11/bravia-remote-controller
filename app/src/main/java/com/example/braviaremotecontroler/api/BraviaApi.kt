package com.example.braviaremotecontroler.api

import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Sony Bravia TVと通信するためのRetrofitインターフェースです。
 */
interface BraviaApi {

    @Headers(
        "Content-Type: text/xml; charset=utf-8",
        "SOAPACTION: \"urn:schemas-sony-com:service:IRCC:1#X_SendIRCC\""
    )
    @POST("sony/ircc")
    suspend fun sendIrcc(
        @Header("X-Auth-PSK") psk: String,
        @Body body: RequestBody
    ): Response<Unit>

    @POST("sony/system")
    suspend fun system(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>

    @POST("sony/audio")
    suspend fun audio(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>

    @POST("sony/appControl")
    suspend fun appControl(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>

    @POST("sony/avContent")
    suspend fun avContent(
        @Header("X-Auth-PSK") psk: String,
        @Body body: JsonRpcRequest
    ): Response<JsonRpcResponse>
}

data class JsonRpcRequest(
    val method: String,
    val id: Int = 1,
    val params: List<Any> = emptyList(),
    val version: String = "1.0"
)

data class JsonRpcResponse(
    val id: Int,
    val result: List<Any>?,
    val error: List<Any>?
)
