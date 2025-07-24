package com.ssafy.glim.core.domain.model

data class QuoteSummary(
    val content: String,
    val page: Int,
    val quoteId: Long,
    val views: Long
)