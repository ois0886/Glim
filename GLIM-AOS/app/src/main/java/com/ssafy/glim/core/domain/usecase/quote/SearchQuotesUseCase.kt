package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class SearchQuotesUseCase
@Inject
constructor(
    private val quoteRepository: QuoteRepository,
) {
    operator fun invoke(query: String) = quoteRepository.searchQuotes(query)
}
