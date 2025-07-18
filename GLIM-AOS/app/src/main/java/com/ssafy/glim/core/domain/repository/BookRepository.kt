package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Book
import kotlinx.coroutines.flow.Flow

interface BookRepository {

    fun searchBooks(query: String): Flow<List<Book>>
}