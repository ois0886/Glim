package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.Content
import com.ssafy.glim.core.data.dto.response.QuoteSearchResultResponse
import com.ssafy.glim.core.domain.model.QuoteSearchResult
import com.ssafy.glim.core.domain.model.QuoteSummary

fun QuoteSearchResultResponse.toDomain() =
    QuoteSearchResult(
        currentPage = currentPage,
        totalPages = totalPages,
        totalResults = totalResults,
        quoteSummaries = contents.map { it.toDomain() }
    )

private fun Content.toDomain(): QuoteSummary {
    return QuoteSummary(
        content = this.content,
        page = this.page.toString(),
        quoteId = this.quoteId,
        views = this.views,
        bookTitle = this.bookTitle,
        likes = this.likes,
        isLiked = this.isLiked
    )
}
