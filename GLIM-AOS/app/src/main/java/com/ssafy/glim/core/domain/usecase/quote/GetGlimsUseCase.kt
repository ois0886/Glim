package com.ssafy.glim.core.domain.usecase.quote

import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetGlimsUseCase
    @Inject
    constructor(
        private val repository: QuoteRepository,
    ) {
        operator fun invoke(
            page: Int,
            size: Int,
        ): Flow<List<Glim>> =
            repository.getGlims(page, size, "")
    }
