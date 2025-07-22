package com.ssafy.glim.core.data.repository

import com.google.android.gms.common.api.Response
import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.repository.BookRepository
import com.ssafy.glim.feature.bookdetail.BookDetail
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class BookRepositoryImpl @Inject constructor(
    private val bookRemoteDataSource: BookRemoteDataSource
) : BookRepository {

    override fun searchBooks(query: String): Flow<Result<List<Book>>> = flow {
        emit(
            bookRemoteDataSource.getBooks(query)
        )
    }

    override fun getBookDetail(isbn: Long): Flow<Result<BookDetail>> = flow {
        TODO("Not yet implemented")
    }

    override fun updateViewCount(isbn: Long): Flow<Result<Unit>> = flow {
        emit(
            bookRemoteDataSource.updateViewCount(isbn)
        )
    }
}

