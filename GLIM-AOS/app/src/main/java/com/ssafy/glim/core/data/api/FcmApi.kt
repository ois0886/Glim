package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.FCMRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface FcmApi {

    @POST("/api/v1/fcm/token")
    suspend fun sendToken(@Body request: FCMRequest)

}
