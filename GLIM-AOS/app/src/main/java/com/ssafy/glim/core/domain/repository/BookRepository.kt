package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.bookdetail.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun searchBooks(query: String): Flow<Result<List<Book>>>

    fun getBookDetail(isbn: Long): Flow<Result<BookDetail>>

    fun updateViewCount(isbn: Long): Flow<Result<Unit>>
}