package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.SearchHistoryResponse
import retrofit2.http.GET

interface SearchQueryService {
    @GET("api/v1/searches/popular")
    suspend fun getPopularSearchQuery(): List<SearchHistoryResponse>
}
