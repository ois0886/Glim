package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.SearchItem
import kotlinx.coroutines.flow.Flow

interface SearchQueryRepository {
    fun getPopularSearchQueries(): Flow<List<SearchItem>>

    fun getRecentSearchQueries(): Flow<List<SearchItem>>

    fun saveRecentSearchQuery(query: String): Flow<Unit>

    fun deleteRecentSearchQuery(query: String): Flow<Unit>
}
