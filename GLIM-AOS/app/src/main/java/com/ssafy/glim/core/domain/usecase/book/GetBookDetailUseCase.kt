package com.ssafy.glim.core.domain.usecase.book

import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(isbn: String?, bookId: Long?) = bookRepository.getBookDetail(isbn, bookId)
}
