package com.ssafy.glim.core.data.dto.request

import com.ssafy.glim.core.domain.model.Book
import kotlinx.serialization.Serializable

@Serializable
data class QuoteRequest(
    val visibility: String = "PUBLIC",
    val content: String,
    val page: Long = 0,
    val isbn: String,
    val bookCreateData: BookCreateData
)

@Serializable
data class BookCreateData(
    val title: String,
    val author: String,
    val translator: String,
    val category: String,
    val categoryId: Long,
    val publisher: String,
    val description: String,
    val isbn: String,
    val isbn13: String,
    val publishedDate: String,
    val coverUrl: String,
    val linkUrl: String
)

fun Book.toRequestDto() = BookCreateData(
    title = title,
    author = author,
    translator = translator,
    category = categoryName,
    categoryId = categoryId,
    publisher = publisher,
    description = description,
    isbn = isbn,
    isbn13 = isbn13,
    publishedDate = pubDate,
    coverUrl = cover,
    linkUrl = link
)
