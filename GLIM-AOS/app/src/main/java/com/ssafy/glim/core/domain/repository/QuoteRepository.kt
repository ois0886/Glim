package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {
    fun searchQuotes(query: String): Flow<List<Quote>>

    fun getGlims(
        page: Int = 0,
        size: Int = 10,
        sort: String,
    ): Flow<List<Glim>>
}
