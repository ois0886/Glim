package com.ssafy.glim.core.domain.usecase.book

import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class SearchBooksUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    suspend operator fun invoke(
        query: String,
        page: Int,
        searchQueryType: String
    ) = bookRepository.searchBooks(query, page, searchQueryType)
}
