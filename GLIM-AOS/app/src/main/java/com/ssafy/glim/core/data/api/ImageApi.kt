package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.GenerateRequest
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ImageApi {
    @POST("api/v1/images")
    suspend fun generateImage(
        @Body request: GenerateRequest,
    ): Response<ResponseBody>
}
