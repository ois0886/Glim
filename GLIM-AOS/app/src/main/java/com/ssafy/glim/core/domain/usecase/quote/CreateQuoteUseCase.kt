package com.ssafy.glim.core.domain.usecase.quote

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class CreateQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(
        content: String,
        isbn: String,
        book: Book,
        bitmap: Bitmap
    ) = quoteRepository.createQuote(
        content = content,
        isbn = isbn,
        book = book,
        bitmap = bitmap
    )
}