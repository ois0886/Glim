package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.bookdetail.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    suspend fun searchBooks(query: String): List<Book>

    suspend fun updateBookViewCount(isbn: Long)
}