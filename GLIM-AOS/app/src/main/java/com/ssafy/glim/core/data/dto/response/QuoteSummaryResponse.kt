package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class QuoteSummaryResponse(
    val content: String,
    val page: Long,
    val quoteId: Long,
    val views: Long,
    val likeCount: Long,
    val liked: Boolean
)
