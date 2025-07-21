package com.ssafy.glim.feature.bookdetail

import com.ssafy.glim.core.domain.model.Quote

data class BookDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookDetail: BookDetail = BookDetail(),
    val isDescriptionExpanded: Boolean = false,
    val isAuthorDescriptionExpanded: Boolean = false
)

data class BookDetail(
    val title: String = "",
    val subTitle: String = "",
    val author: String = "",
    val publisher: String = "",
    val publicationDate: String = "",
    val category: String = "",
    val price: Int = 0,
    val isbn: String = "",
    val description: String = "",
    val authorDescription: String? = null,
    val coverImageUrl: String? = null,
    val quotes: List<Quote> = emptyList(),
    val marketUrl: String = "https://www.naver.com",
) {
    val priceText
        //comma-separated price with "원" suffix
        get() = if (price > 0) "${
            price.toString().replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
        }원" else "가격 정보 없음"

}

sealed class BookDetailSideEffect {
    data class ShowToast(val message: String) : BookDetailSideEffect()
    data class OpenUrl(val url: String) : BookDetailSideEffect()
}