package com.ssafy.glim.core.data.service

import retrofit2.http.GET

interface CurationApiService {
    @GET("/api/v1/curations/main")
    suspend fun getMainCurations(): List<CurationItemResponse>
}