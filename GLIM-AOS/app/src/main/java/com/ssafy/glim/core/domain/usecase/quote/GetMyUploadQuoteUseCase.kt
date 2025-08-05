package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.model.UploadQuote
import com.ssafy.glim.core.domain.repository.QuoteRepository
import jakarta.inject.Inject

class GetMyUploadQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(): List<UploadQuote> =
        quoteRepository.getMyUploadQuotes()
}
