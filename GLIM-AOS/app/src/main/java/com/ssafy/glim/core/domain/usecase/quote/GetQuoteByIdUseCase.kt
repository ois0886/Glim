package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class GetQuoteByIdUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
) {
    suspend operator fun invoke(quoteId: Long) =
        quoteRepository.getQuoteById(quoteId)
}
