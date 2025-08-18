package com.ssafy.glim.core.domain.model

data class QuoteSummary(
    val content: String,
    val page: String,
    val quoteId: Long,
    val views: Long,
    val bookTitle: String = "",
    val likes: Long,
    val isLiked: Boolean,
    val createdAt: String = ""
)
