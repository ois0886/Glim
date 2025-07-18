package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Quote
import kotlinx.coroutines.flow.Flow

interface QuoteRepository {

    fun searchQuotes(query: String): Flow<List<Quote>>
}