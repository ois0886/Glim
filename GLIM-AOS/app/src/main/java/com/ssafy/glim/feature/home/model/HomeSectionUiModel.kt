package com.ssafy.glim.feature.home.model

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Glim

sealed class HomeSectionUiModel {
    abstract val id: String
    abstract val title: String

    data class GlimSection(
        override val id: String,
        override val title: String,
        val glims: List<Glim>
    ) : HomeSectionUiModel()

    data class BookSection(
        override val id: String,
        override val title: String,
        val books: List<Book>
    ) : HomeSectionUiModel()
}
