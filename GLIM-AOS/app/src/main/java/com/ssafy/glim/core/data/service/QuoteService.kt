package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.GlimResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QuoteService {
    @GET("quotes")
    suspend fun getGlims(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String,
    ): List<GlimResponse>
}