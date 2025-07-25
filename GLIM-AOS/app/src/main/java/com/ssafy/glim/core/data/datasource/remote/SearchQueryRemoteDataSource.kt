package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.service.SearchQueryService
import javax.inject.Inject

class SearchQueryRemoteDataSource @Inject constructor(
    private val searchService: SearchQueryService
) {
    suspend fun getPopularSearches() = searchService.getPopularSearchQuery()
}
