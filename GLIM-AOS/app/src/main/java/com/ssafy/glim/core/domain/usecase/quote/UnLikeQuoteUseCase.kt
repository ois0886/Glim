package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class UnLikeQuoteUseCase @Inject constructor(
    private val quoteRepository: QuoteRepository
){
    suspend operator fun invoke(quoteId: Long) = quoteRepository.unLikeQuote(quoteId)
}
