package com.ssafy.glim.core.domain.model

data class LikedQuote (
    val quoteId: Long,
    val content: String,
    val views: Long,
    val page: Int,
    val likeCount: Long,
    val liked: Boolean,
)
