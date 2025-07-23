package com.ssafy.glim.core.data.dto.response

data class GlimResponse(
    val quoteId: Long,
    val quoteImageName: String,
    val quoteViews: Long?,
    val bookId: Long,
    val bookTitle: String,
    val author: String,
    val bookCoverUrl: String,
)
