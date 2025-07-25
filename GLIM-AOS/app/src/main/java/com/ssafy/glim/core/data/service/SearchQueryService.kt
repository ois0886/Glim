package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.SearchHistoryResponse
import retrofit2.http.GET
import retrofit2.http.Headers

interface SearchQueryService {
    @GET("api/v1/searches/popular")
    suspend fun getPopularSearchQuery(
    ): List<SearchHistoryResponse>
}

