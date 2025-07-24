package com.ssafy.glim.core.domain.model

data class Quote(
    val author: String,
    val bookCoverUrl: String,
    val bookId: Long,
    val bookTitle: String,
    val page: Int,
    val publisher: Any,
    val quoteId: Long,
    val quoteImageName: String,
    val quoteViews: Any,
    val isLike: Boolean = false,
    val likes: Long = 0L,
)
