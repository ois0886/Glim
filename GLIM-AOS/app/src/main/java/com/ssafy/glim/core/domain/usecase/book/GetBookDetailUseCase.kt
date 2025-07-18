package com.ssafy.glim.core.domain.usecase.book

import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class GetBookDetailUseCase @Inject constructor(
    private val bookRepository: BookRepository,
) {
    operator fun invoke(bookId: String) = bookRepository.getBookDetail(bookId)
}