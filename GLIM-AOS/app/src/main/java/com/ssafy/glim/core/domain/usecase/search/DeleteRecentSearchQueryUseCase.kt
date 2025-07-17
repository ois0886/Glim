package com.ssafy.glim.core.domain.usecase.search

import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import javax.inject.Inject

class DeleteRecentSearchQueryUseCase @Inject constructor(
    private val searchQueryRepository: SearchQueryRepository
) {
    operator fun invoke(query: String) = searchQueryRepository.deleteRecentSearchQuery(query)
}