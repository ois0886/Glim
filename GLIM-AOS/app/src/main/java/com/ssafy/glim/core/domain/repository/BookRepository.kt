package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.feature.bookdetail.BookDetail
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun searchBooks(query: String): Flow<List<Book>>

    fun getBookDetail(isbn: String): Flow<BookDetail>
}