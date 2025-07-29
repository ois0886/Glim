package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.data.dto.response.QuoteSummaryResponse
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.QuoteSummary

fun QuoteResponse.toDomain() =
    Quote(
        author = author,
        bookCoverUrl = bookCoverUrl,
        bookId = bookId,
        bookTitle = bookTitle,
        page = page,
        publisher = publisher,
        quoteId = quoteId,
        quoteImageName = quoteImageName,
        quoteViews = 0
    )

fun QuoteSummaryResponse.toDomain() =
    QuoteSummary(
        content = content,
        quoteId = quoteId,
        page = if (page > 0) page.toString() else "-",
        views = views,
    )
