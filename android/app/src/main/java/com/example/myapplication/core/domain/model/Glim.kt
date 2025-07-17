package com.example.myapplication.core.domain.model

data class Glim(
    val id: Long,
    val imgUrl: String,
    val isLike: Boolean,
    val likes: Int,
    val bookTitle: String,
    val bookAuthor: String,
    val bookImgUrl: String,
    val pageInfo: String = "",
)
