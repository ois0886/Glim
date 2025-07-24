package com.ssafy.glim.core.domain.usecase.curation

import com.ssafy.glim.core.domain.model.Curation
import com.ssafy.glim.core.domain.repository.CurationRepository
import javax.inject.Inject

class GetMainCurationsUseCase @Inject constructor(
    private val repository: CurationRepository
) {
    suspend operator fun invoke(): List<Curation> =
        repository.getMainCurations()
}
