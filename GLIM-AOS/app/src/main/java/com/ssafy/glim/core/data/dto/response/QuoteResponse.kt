package com.ssafy.glim.core.data.dto.response

data class QuoteResponse(
    val author: String,
    val bookCoverUrl: String,
    val bookId: Long,
    val bookTitle: String,
    val page: Int,
    val publisher: Any,
    val quoteId: Long,
    val quoteImageName: String,
    val quoteViews: Any
)