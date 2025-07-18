package com.ssafy.glim.feature.bookdetail

import com.ssafy.glim.core.domain.model.Quote

data class BookDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookDetail: BookDetail = BookDetail()
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
)

sealed class BookDetailSideEffect {
    data class ShowToast(val message: String) : BookDetailSideEffect()
    data class OpenUrl(val url: String) : BookDetailSideEffect()
}