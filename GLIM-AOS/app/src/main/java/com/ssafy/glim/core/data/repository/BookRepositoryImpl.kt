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

    override fun getBookDetail(isbn: String?, bookId: Long?): Book {
        // TODO: bookId로 상세 정보 요청

        Log.d("BookRepositoryImpl", "getBookDetail called with isbn: $isbn, bookId: $bookId")
        return books.first {
            it.isbn == isbn
        }
    }
}
