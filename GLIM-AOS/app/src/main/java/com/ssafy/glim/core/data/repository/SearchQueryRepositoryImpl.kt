package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.SearchQueryRemoteDataSource
import com.ssafy.glim.core.data.datastore.SearchQueryDataStore
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import javax.inject.Inject

class SearchQueryRepositoryImpl
@Inject
constructor(
    private val searchQueryDataStore: SearchQueryDataStore,
    private val searchHistoryDataSource: SearchQueryRemoteDataSource
) : SearchQueryRepository {
    override suspend fun getPopularSearchQueries() = searchHistoryDataSource.getPopularSearches().mapIndexed { index, title ->
        SearchItem(
            text = title,
            rank = index + 1,
            type = "전체"
        )
    }

    override fun getRecentSearchQueries() = searchQueryDataStore.getSearchHistory()

    override suspend fun saveRecentSearchQuery(query: String) = searchQueryDataStore.addSearchQuery(query)

    override suspend fun deleteRecentSearchQuery(query: String) = searchQueryDataStore.removeSearchQuery(query)
}
