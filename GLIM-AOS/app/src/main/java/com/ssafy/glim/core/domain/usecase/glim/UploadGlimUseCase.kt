package com.ssafy.glim.core.domain.usecase.glim

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class UploadGlimUseCase
@Inject
constructor(
    private val quoteRepository: QuoteRepository,
) {
    operator fun invoke(
        content: String,
        bookId: Long,
        book: Book,
        bitMap: Bitmap
    ) = quoteRepository.createGlim(content, bookId, book, bitMap)
}
