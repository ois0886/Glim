package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class QuoteResponse(
    val author: String,
    val bookCoverUrl: String,
    val bookId: Long,
    val bookTitle: String,
    val page: Int,
    val publisher: String,
    val quoteId: Long,
    val quoteImageName: String,
    val quoteViews: Long,
)
