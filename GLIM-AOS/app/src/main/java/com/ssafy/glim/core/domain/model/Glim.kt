package com.ssafy.glim.core.domain.model

data class Glim(
    val id: Long,
    val bookId: Long = -1,
    val imgUrl: String,
    val isLike: Boolean,
    val likes: Long,
    val bookTitle: String,
    val bookAuthor: String,
    val bookImgUrl: String,
    val pageInfo: String = "",
)
