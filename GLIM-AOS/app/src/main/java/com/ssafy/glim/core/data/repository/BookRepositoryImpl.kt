package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookRemoteDataSource: BookRemoteDataSource
) : BookRepository {

    private val books = mutableListOf<Book>()

    override suspend fun searchBooks(query: String, page: Int): List<Book> {
        if(page == 0) books.clear()

        val searchedBooks = bookRemoteDataSource.getBooks(query, page).map { it.toDomain() }
        books.addAll(searchedBooks)

        return searchedBooks
    }

    override suspend fun updateBookViewCount(isbn: Long) =
        bookRemoteDataSource.updateBookViewCount(isbn)

    override fun getBookDetail(bookId: Long) = books.first {
        it.itemId == bookId
    }
}
