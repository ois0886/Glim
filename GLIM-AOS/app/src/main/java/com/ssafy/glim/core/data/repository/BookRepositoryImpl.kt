package com.ssafy.glim.core.data.repository

import android.util.Log
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
        if (page == 0) books.clear()

        val searchedBooks = bookRemoteDataSource.getBooks(query, page).map { it.toDomain() }
        books.addAll(searchedBooks)

        return searchedBooks
    }

    override suspend fun updateBookViewCount(isbn: Long) =
        bookRemoteDataSource.updateBookViewCount(isbn)

    override suspend fun getBookDetail(isbn: String?, bookId: Long?): Book {
        if (isbn == null) {
            if (bookId == null) {
                throw IllegalArgumentException("Either isbn or bookId must be provided")
            }
            return bookRemoteDataSource.getBook(bookId).toDomain()
        }

        Log.d("BookRepositoryImpl", "getBookDetail called with isbn: $isbn, bookId: $bookId")
        return books.first {
            it.isbn == isbn
        }
    }
}
