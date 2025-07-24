package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.SearchItem
import kotlinx.coroutines.flow.Flow

interface SearchQueryRepository {

    fun getPopularSearchQueries(): List<SearchItem>

    fun getRecentSearchQueries(): List<SearchItem>

    fun saveRecentSearchQuery(query: String)

    fun deleteRecentSearchQuery(query: String)
}