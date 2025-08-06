package com.ssafy.glim.core.data.dto.response

data class UploadQuoteResponse(
    val quoteId: Long,
    val content: String,
    val views: Long,
    val page: Int,
    val likeCount: Long,
    val createdAt: String,
    val liked: Boolean,
)
