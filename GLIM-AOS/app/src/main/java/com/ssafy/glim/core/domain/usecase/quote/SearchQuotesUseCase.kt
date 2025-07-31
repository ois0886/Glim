package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.repository.QuoteRepository
import javax.inject.Inject

class SearchQuotesUseCase
@Inject
constructor(
    private val quoteRepository: QuoteRepository,
) {
    suspend operator fun invoke(query: String, page: Int = 0, size: Int = 10) =
        quoteRepository.searchQuotes(query, page, size)
}
