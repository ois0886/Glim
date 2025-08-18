package com.ssafy.glim.core.domain.usecase.book

import com.ssafy.glim.core.domain.repository.BookRepository
import javax.inject.Inject

class GetCachedBookDetail @Inject constructor(
    private val bookRepository: BookRepository
) {
    operator fun invoke(bookId: Long) = bookRepository.getCachedBookDetail(bookId)
}
