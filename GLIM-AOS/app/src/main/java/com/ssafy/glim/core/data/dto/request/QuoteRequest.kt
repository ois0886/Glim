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

/*
    o : BookResponse에 있음
    x : BookResponse에 없음
    ㅁ : BookResponse에 있지만 이름이 다름
 */
@Serializable
data class BookCreateData(
    val title: String, // o
    val author: String, // o
    val translator: String, // x
    val category: String, // ㅁ  categoryName 추정
    val categoryId: Long, // ㅁ  categoryId 추정
    val publisher: String, // o
    val description: String, // o
    val isbn: String, // o
    val isbn13: String, // o
    val publishedDate: String, // ㅁ pubDate 추정
    val coverUrl: String, // ㅁ cover 추정
    val linkUrl: String // ㅁ link 추정
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
