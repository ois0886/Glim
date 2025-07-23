package com.ssafy.glim.core.domain.model

data class CurationContent(
    val bookId: Long?,
    val title: String,
    val author: String,
    val publisher: String,
    val coverUrl: String?,
    val quoteId: Long?,
    val imageName: String?
)
