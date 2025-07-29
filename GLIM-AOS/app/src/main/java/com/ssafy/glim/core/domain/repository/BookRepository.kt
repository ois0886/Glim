package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book

interface BookRepository {

    suspend fun searchBooks(query: String, page: Int): List<Book>

    suspend fun updateBookViewCount(isbn: Long)

    fun getBookDetail(isbn: String?, bookId: Long?): Book
}
