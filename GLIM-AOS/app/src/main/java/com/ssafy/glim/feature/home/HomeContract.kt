package com.ssafy.glim.feature.home

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote

data class HomeUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val sections: List<HomeSectionUiModel> = emptyList(),
)

sealed interface HomeSideEffect {
    data class ShowError(val message: String) : HomeSideEffect
}

sealed class HomeSectionUiModel {
    abstract val id: String
    abstract val title: String

    data class QuoteSection(
        override val id: String,
        override val title: String,
        val quotes: List<Quote>
    ) : HomeSectionUiModel()

    data class BookSection(
        override val id: String,
        override val title: String,
        val books: List<Book>
    ) : HomeSectionUiModel()
}

