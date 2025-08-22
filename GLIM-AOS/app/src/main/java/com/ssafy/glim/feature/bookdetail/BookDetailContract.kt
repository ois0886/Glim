package com.ssafy.glim.feature.bookdetail

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.QuoteSummary

data class BookDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val bookDetail: Book = Book(),
    val quoteSummaries: List<QuoteSummary> = emptyList(),
    val isDescriptionExpanded: Boolean = false,
    val isAuthorDescriptionExpanded: Boolean = false
)

sealed class BookDetailSideEffect {
    data class ShowToast(val message: String) : BookDetailSideEffect()
    data class OpenUrl(val url: String) : BookDetailSideEffect()
}
