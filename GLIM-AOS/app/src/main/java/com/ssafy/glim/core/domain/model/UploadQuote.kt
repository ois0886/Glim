package com.ssafy.glim.core.domain.model

data class UploadQuote(
    val quoteId: Long,
    val content: String,
    val views: Long,
    val page: Int,
    val likeCount: Long,
    val createdAt: String,
    val liked: Boolean,
)
