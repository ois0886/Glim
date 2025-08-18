package com.ssafy.glim.core.data.datasource.local

import android.util.Log
import com.ssafy.glim.core.domain.model.Quote
import javax.inject.Inject

class QuoteLocalDataSource @Inject constructor() {
    val quotes = hashSetOf<Quote>()

    fun addQuotes(quoteList: List<Quote>) {
        quotes.addAll(quoteList)
        Log.d("QuoteLocalDataSource", "Quotes added: ${quoteList.size}, Total quotes: ${quotes.size}")
    }

    fun getQuote(quoteId: Long): Quote {
        Log.d("QuoteLocalDataSource", "Searching for quote with ID: $quoteId")
        return quotes.first {
            it.quoteId == quoteId
        }
    }
}
