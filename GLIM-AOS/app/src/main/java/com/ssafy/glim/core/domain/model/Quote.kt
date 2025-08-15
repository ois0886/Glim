package com.ssafy.glim.core.domain.model

import com.ssafy.glim.BuildConfig

data class Quote(
    val content: String = "",
    val author: String,
    val bookCoverUrl: String,
    val bookId: Long,
    val bookTitle: String,
    val page: Int,
    val publisher: Any,
    val quoteId: Long,
    val quoteImageName: String,
    val quoteViews: Long,
    val isLike: Boolean = false,
    val likes: Long = 0L,
) {
    val quoteImageUrl get() = "${BuildConfig.BASE_URL}/images/$quoteImageName"
}
