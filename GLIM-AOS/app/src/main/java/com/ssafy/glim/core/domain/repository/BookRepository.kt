package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book

interface BookRepository {

    suspend fun searchBooks(query: String): List<Book>

    suspend fun updateBookViewCount(isbn: Long)

    fun getBookDetail(bookId: Long): Book
}
