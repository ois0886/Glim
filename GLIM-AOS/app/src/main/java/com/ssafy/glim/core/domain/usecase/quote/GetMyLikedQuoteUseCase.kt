package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.model.LikedQuote
import com.ssafy.glim.core.domain.repository.QuoteRepository
import jakarta.inject.Inject

class GetMyLikedQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(): List<LikedQuote> =
        quoteRepository.getMyLikedQuotes()
}
