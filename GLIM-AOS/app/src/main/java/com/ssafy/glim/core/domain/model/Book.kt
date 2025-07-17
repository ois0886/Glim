package com.ssafy.glim.core.domain.model

data class Book(
    val title: String,
    val author: String,
    val publisher: String,
    val publicationDate: String,
    val isbn: String,
    val description: String,
    val coverImageUrl: String? = null,
)
