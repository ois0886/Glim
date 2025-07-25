package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.SearchItem
import kotlinx.coroutines.flow.Flow

interface SearchQueryRepository {
    suspend fun getPopularSearchQueries(): List<SearchItem>

    fun getRecentSearchQueries(): Flow<List<String>>

    suspend fun saveRecentSearchQuery(query: String)

    suspend fun deleteRecentSearchQuery(query: String)
}
