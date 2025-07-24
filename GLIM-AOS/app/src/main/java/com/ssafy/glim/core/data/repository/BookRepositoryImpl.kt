package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookRemoteDataSource: BookRemoteDataSource
) : BookRepository {

    override suspend fun searchBooks(query: String): List<Book> =
        bookRemoteDataSource.getBooks(query).map { it.toDomain() }

    override suspend fun updateBookViewCount(isbn: Long) =
        bookRemoteDataSource.updateBookViewCount(isbn)
}
