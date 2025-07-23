package com.ssafy.glim.core.domain.model

data class Glim(
    val id: Long = -1,
    val bookId: Long = -1,
    val imgUrl: String = "",
    val isLike: Boolean = false,
    val likes: Long = 0,
    val bookTitle: String = "",
    val bookAuthor: String = "",
    val bookImgUrl: String = "",
    val pageInfo: String = "",
)
