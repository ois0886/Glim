package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class QuoteRepositoryImpl @Inject constructor(
    private val quoteDataSource : QuoteRemoteDataSource
) : QuoteRepository{
    override fun searchQuotes(query: String): Flow<List<Quote>> {
        TODO("Not yet implemented")
    }

    override suspend fun getGlims(
        page: Int,
        size: Int,
        sort: String
    ): List<Glim> {
        val dto = quoteDataSource.fetchQuotes(page, size, sort)
        return dto.map { it.toDomain() }
    }

}