package com.ssafy.glim.core.domain.model

import com.ssafy.glim.core.common.extensions.toCommaSeparatedPrice
import kotlinx.serialization.Serializable

// 검색 화면에서 북 정보를 보내기 위해 Serializable로 선언

@Serializable
data class Book(
    val adult: Boolean = false,
    val author: String = "",
    val categoryId: Long = 0L,
    val categoryName: String = "",
    val cover: String = "",
    val description: String = "",
    val fixedPrice: Boolean = false,
    val isbn: String = "",
    val itemId: Long = 0L,
    val link: String = "",
    val priceSales: Long = 0L,
    val priceStandard: Long = 0L,
    val pubDate: String = "",
    val publisher: String = "",
    val stockStatus: String = "",
    val title: String = "",
){
    val priceText
        get() = if (priceStandard > 0) "${
            priceStandard.toString().toCommaSeparatedPrice()
        }원" else "가격 정보 없음"
}
