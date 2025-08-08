package com.ssafy.glim.core.data.api

import retrofit2.http.GET

interface SearchQueryApi {
    @GET("api/v1/search-keywords/popular")
    suspend fun getPopularSearchQuery(): List<String>
}
