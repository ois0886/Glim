package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuoteSearchResultResponse(
    val contents: List<Content>,
    val currentPage: Int,
    val totalPages: Int,
    val totalResults: Int
)

@Serializable
data class Content(
    val bookTitle: String,
    val content: String,
    @SerialName("isliked") val isLiked: Boolean,
    val likes: Long,
    val page: Int,
    val quoteId: Long,
    val views: Long
)
