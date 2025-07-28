package com.ssafy.glim.core.domain.repository

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.QuoteSummary

interface QuoteRepository {

    fun searchQuotes(query: String): List<Quote>

    suspend fun getQuotes(
        page: Int = 0,
        size: Int = 10,
        sort: String
    ): List<Quote>

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
}
