package com.ssafy.glim.core.data.dto.response

data class LikedQuoteResponse(
    val quoteId: Long,
    val content: String,
    val views: Long,
    val page: Int,
    val likeCount: Long,
    val liked: Boolean,
)
