package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.core.data.dto.response.LikedQuoteResponse
import com.ssafy.glim.core.data.dto.response.QuoteResponse
import com.ssafy.glim.core.data.dto.response.QuoteSummaryResponse
import com.ssafy.glim.core.data.dto.response.UploadQuoteResponse
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
        quoteViews = quoteViews,
        likes = likeCount,
        isLike = liked
    )

fun UploadQuoteResponse.toDomain() = QuoteSummary(
    content = content,
    page = page.toString(),
    quoteId = quoteId,
    views = views,
    likes = likeCount,
    isLiked = liked,
    createdAt = createdAt
)

fun LikedQuoteResponse.toDomain() = QuoteSummary(
    content = content,
    page = page.toString(),
    quoteId = quoteId,
    views = views,
    likes = likeCount,
    isLiked = liked
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
