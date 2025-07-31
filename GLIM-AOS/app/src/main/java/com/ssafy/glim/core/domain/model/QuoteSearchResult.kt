package com.ssafy.glim.core.domain.model

data class QuoteSearchResult(
    val currentPage: Int,
    val totalPages: Int,
    val totalResults: Int,
    val quoteSummaries: List<QuoteSummary>
)
