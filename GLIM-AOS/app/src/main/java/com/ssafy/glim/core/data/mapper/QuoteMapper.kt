package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.LikedQuoteResponse
import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.data.dto.response.QuoteSummaryResponse
import com.ssafy.glim.core.data.dto.response.UploadQuoteResponse
import com.ssafy.glim.core.domain.model.LikedQuote
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.model.UploadQuote

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
        quoteViews = quoteViews,
        likes = likeCount,
        isLike = liked
    )

fun UploadQuoteResponse.toDomain() = UploadQuote(
    quoteId = quoteId,
    content = content,
    views = views,
    page = page,
    likeCount = likeCount,
    createdAt = createdAt,
    liked = liked
)

fun LikedQuoteResponse.toDomain() = LikedQuote(
    quoteId = quoteId,
    content = content,
    views = views,
    page = page,
    likeCount = likeCount,
    liked = liked
)

fun QuoteSummaryResponse.toDomain() =
    QuoteSummary(
        content = content,
        quoteId = quoteId,
        page = if (page > 0) page.toString() else "-",
        views = views,
        likes = likeCount,
        isLiked = liked
    )
