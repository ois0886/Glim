package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.FcmRequest
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface FcmApi {

    @POST("/api/v1/fcm/token")
    suspend fun sendToken(@Body request: FcmRequest)

    @POST("/api/v1/fcm/token/{deviceId}/status")
    suspend fun deleteToken(@Path("deviceId") deviceId: String)
}
