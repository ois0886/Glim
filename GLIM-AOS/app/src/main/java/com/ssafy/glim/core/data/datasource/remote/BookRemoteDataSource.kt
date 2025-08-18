package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.BookApi
import javax.inject.Inject

class BookRemoteDataSource @Inject constructor(
    private val bookApi: BookApi
) {
    suspend fun getBooks(
        keyword: String,
        page: Int,
        queryType: String
    ) = bookApi.getBooks(keyword, page, queryType)

    suspend fun updateBookViewCount(
        bookId: Long,
    ) = bookApi.updateViewCount(bookId)

    suspend fun getBook(bookId: Long) = bookApi.getBook(bookId)
}
