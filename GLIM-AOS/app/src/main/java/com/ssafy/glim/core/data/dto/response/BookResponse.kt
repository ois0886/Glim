package com.ssafy.glim.core.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class BookResponse(
    val adult: Boolean,
    val author: String,
    val categoryId: Long,
    val categoryName: String,
    val cover: String,
    val customerReviewRank: Long,
    val description: String,
    val fixedPrice: Boolean,
    val isbn: String,
    val isbn13: String,
    val itemId: Long,
    val link: String,
    val mallType: String,
    val mileage: Long,
    val priceSales: Long,
    val priceStandard: Long,
    val pubDate: String,
    val publisher: String,
    val salesPoint: Long,
    val stockStatus: String,
    val title: String
)