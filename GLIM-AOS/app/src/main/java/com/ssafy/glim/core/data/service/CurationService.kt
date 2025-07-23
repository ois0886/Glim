package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import retrofit2.http.GET

interface CurationService {
    @GET("/api/v1/curations/main")
    suspend fun getMainCurations(): List<CurationItemResponse>
}