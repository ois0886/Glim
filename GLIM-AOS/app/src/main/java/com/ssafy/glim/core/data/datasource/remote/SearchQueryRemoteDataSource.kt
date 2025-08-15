package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.SearchQueryApi
import javax.inject.Inject

class SearchQueryRemoteDataSource @Inject constructor(
    private val searchService: SearchQueryApi
) {
    suspend fun getPopularSearches() = searchService.getPopularSearchQuery()
}
