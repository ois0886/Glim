package com.ssafy.glim.core.domain.model

data class Quote(
    val text: String,
    val bookTitle: String,
    val page: String? = null,
    val likes: Int
)
