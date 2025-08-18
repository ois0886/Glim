package com.ssafy.glim.feature.home.model

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Quote

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
