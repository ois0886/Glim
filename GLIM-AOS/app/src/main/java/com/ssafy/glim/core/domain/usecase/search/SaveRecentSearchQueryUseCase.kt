package com.ssafy.glim.core.domain.usecase.search

import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import javax.inject.Inject

class SaveRecentSearchQueryUseCase
@Inject
constructor(
    private val searchQueryRepository: SearchQueryRepository,
) {
    operator fun invoke(query: String) =
        searchQueryRepository.saveRecentSearchQuery(query)
}
