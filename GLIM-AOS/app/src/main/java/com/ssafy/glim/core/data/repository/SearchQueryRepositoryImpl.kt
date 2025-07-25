package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.SearchQueryRemoteDataSource
import com.ssafy.glim.core.data.datastore.SearchHistoryDataStore
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import javax.inject.Inject

class SearchQueryRepositoryImpl
@Inject
constructor(
    private val searchHistoryDataStore: SearchHistoryDataStore,
    private val searchHistoryDataSource: SearchQueryRemoteDataSource
) : SearchQueryRepository {
        override suspend fun getPopularSearchQueries() = searchHistoryDataSource.getPopularSearches().first().searchHistory.map { title ->
        SearchItem(
            text = title
        )
    }

    override fun getRecentSearchQueries() = searchHistoryDataStore.getSearchHistory()

    override suspend fun saveRecentSearchQuery(query: String) = searchHistoryDataStore.addSearchQuery(query)

    override suspend fun deleteRecentSearchQuery(query: String) = searchHistoryDataStore.removeSearchQuery(query)
}
