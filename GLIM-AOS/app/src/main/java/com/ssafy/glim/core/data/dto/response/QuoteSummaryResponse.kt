package com.ssafy.glim.core.data.dto.response

data class QuoteSummaryResponse(
    val content: String,
    val page: Long,
    val quoteId: Long,
    val views: Long
)
