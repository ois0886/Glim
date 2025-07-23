package com.ssafy.glim.core.data.dto.response

data class CurationItemResponse(
    val curationItemId: Long?,
    val title: String,
    val description: String,
    val curationType: String,
    val contents: List<CurationContentResponse>
)

data class CurationContentResponse(
    val bookId: Long?,
    val bookTitle: String,
    val author: String,
    val publisher: String,
    val bookCoverUrl: String?,
    val quoteId: Long?,
    val imageName: String?
)