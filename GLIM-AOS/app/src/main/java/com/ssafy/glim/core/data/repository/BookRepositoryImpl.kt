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
    private lateinit var recentBookDetail: Book

    override suspend fun searchBooks(
        query: String,
        page: Int,
        searchQueryType: String
    ): List<Book> {
        if (page == 0) books.clear()

        val searchedBooks = bookRemoteDataSource.getBooks(query, page, searchQueryType).map { it.toDomain() }
        books.addAll(searchedBooks)

        return searchedBooks
    }

    override suspend fun updateBookViewCount(isbn: Long) =
        bookRemoteDataSource.updateBookViewCount(isbn)

    override suspend fun getBookDetail(isbn: String?, bookId: Long?): Book {
        val book = if (isbn == null) {
            if (bookId == null) {
                throw IllegalArgumentException("Either isbn or bookId must be provided")
            }
            bookRemoteDataSource.getBook(bookId).toDomain()
        } else {
            Log.d("BookRepositoryImpl", "getBookDetail called with isbn: $isbn, bookId: $bookId")
            books.first {
                it.isbn == isbn
            }
        }
        recentBookDetail = book
        return book
    }

    override fun getCachedBookDetail(bookId: Long): Book {
        if (bookId == recentBookDetail.bookId) return recentBookDetail
        throw Exception("메모리에 저장된 책 정보의 id와 일치하지 않습니다.")
    }
}
