package com.ssafy.glim.core.domain.usecase.search

import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import javax.inject.Inject

class GetPopularSearchQueriesUseCase @Inject constructor(
    private val searchQueryRepository: SearchQueryRepository,
) {
    operator fun invoke() = searchQueryRepository.getPopularSearchQueries()
}