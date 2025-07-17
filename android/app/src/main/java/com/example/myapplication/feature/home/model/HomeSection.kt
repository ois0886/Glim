package com.example.myapplication.feature.home.model

sealed class HomeSectionUiModel {
    abstract val id: String
    abstract val title: String

    data class GlimSection(
        override val id: String,
        override val title: String,
        val glims: List<GlimInfo>
    ) : HomeSectionUiModel()

    data class BookSection(
        override val id: String,
        override val title: String,
        val books: List<BookItem>
    ) : HomeSectionUiModel()
}
