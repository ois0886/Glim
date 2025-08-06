package com.ssafy.glim.core.data.service

import retrofit2.http.GET

interface SearchQueryService {
    @GET("api/v1/search-keywords/popular")
    suspend fun getPopularSearchQuery(): List<String>
}
