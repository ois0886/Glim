package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datastore.SearchHistoryDataStore
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchQueryRepositoryImpl
@Inject
constructor(
    private val searchHistoryDataStore: SearchHistoryDataStore,
) : SearchQueryRepository {
    override fun getPopularSearchQueries(): List<SearchItem> {
        TODO("Not yet implemented")
    }

    override fun getRecentSearchQueries(): Flow<List<String>> = searchHistoryDataStore.getSearchHistory()

    override suspend fun saveRecentSearchQuery(query: String) = searchHistoryDataStore.addSearchQuery(query)

    override suspend fun deleteRecentSearchQuery(query: String) = searchHistoryDataStore.removeSearchQuery(query)
}
