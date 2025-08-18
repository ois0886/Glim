package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BookDetailResponse(
    val author: String,
    val bookId: Long,
    val categoryId: Long,
    val categoryName: String,
    val coverUrl: String,
    val description: String,
    val isbn: String,
    val isbn13: String,
    val linkUrl: String,
    val publishedDate: String,
    val publisher: String,
    val title: String,
    val views: Long
)
