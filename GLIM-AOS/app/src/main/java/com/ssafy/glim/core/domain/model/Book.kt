package com.ssafy.glim.core.domain.model

import com.ssafy.glim.core.common.extensions.toCommaSeparatedPrice
import kotlinx.serialization.Serializable

// 검색 화면에서 북 정보를 보내기 위해 Serializable로 선언

@Serializable
data class Book(
    val bookId: Long = 0L,
    val adult: Boolean = false,
    val author: String = "",
    val categoryId: Long = 0L,
    val categoryName: String = "",
    val cover: String = "",
    val description: String = "",
    val isbn: String = "",
    val isbn13: String = "",
    val link: String = "",
    val priceSales: Long = 0L,
    val priceStandard: Long = 0L,
    val pubDate: String = "",
    val publisher: String = "",
    val title: String = "",
    val translator: String = "",
    val views: Long = 0L
) {
    val priceText
        get() = if (priceStandard > 0) {
            "${
                priceStandard.toString().toCommaSeparatedPrice()
            }원"
        } else {
            "가격 정보 없음"
        }

    val categoryText
        get() = categoryName.split(">").last()
}
