package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.domain.model.Quote

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
        quoteViews = quoteViews
    )
