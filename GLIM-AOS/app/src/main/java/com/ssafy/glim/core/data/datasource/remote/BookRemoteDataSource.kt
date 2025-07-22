package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.service.BookService
import javax.inject.Inject

class BookRemoteDataSource @Inject constructor(
    private val bookService: BookService
){
    suspend fun getBooks(
        keyword: String,
        page: Int = 1,
        queryType: String = "KEYWORD",
    ) = bookService.getBooks(keyword, page, queryType)

    suspend fun updateViewCount(
        bookId: Long,
    ) = bookService.updateViewCount(bookId)
}