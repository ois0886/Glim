package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val adult: Boolean,
    val author: String,
    val categoryId: Long,
    val categoryName: String,
    val coverUrl: String,
    val description: String,
    val isbn: String,
    val isbn13: String,
    val linkUrl: String,
    val mallType: String,
    val priceSales: Long,
    val priceStandard: Long,
    val publishedDate: String,
    val publisher: String,
    val title: String,
    val translator: String? = null,
)
