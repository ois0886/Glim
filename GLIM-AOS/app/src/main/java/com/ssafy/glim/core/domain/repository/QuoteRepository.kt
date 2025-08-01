package com.ssafy.glim.core.domain.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.QuoteSearchResult
import com.ssafy.glim.core.domain.model.QuoteSummary

interface QuoteRepository {

    suspend fun getQuotes(
        page: Int = 0,
        size: Int = 10,
        sort: String
    ): List<Quote>

    suspend fun searchQuotes(
        query: String,
        page: Int,
        size: Int
    ): QuoteSearchResult

    suspend fun createQuote(
        content: String,
        isbn: String,
        book: Book,
        bitmap: Bitmap
    )

    suspend fun updateQuoteViewCount(
        quoteId: Long
    )

    suspend fun getQuoteByIsbn(
        isbn: String
    ): List<QuoteSummary>

    suspend fun getQuoteById(
        quoteId: Long
    ): Quote

    suspend fun likeQuote(quoteId: Long)

    suspend fun unLikeQuote(quoteId: Long)
}
