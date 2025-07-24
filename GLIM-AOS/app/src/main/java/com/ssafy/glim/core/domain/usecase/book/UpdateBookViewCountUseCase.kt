package com.ssafy.glim.core.domain.usecase.book

import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class UpdateBookViewCountUseCase @Inject constructor(
    private val bookRepository: BookRepository
) {
    suspend operator fun invoke(isbn: Long) = bookRepository.updateBookViewCount(isbn)
}