package com.ssafy.glim.core.domain.usecase.glim

import com.ssafy.glim.core.domain.repository.GlimRepository
import javax.inject.Inject

class GetGlimsUseCase
@Inject
constructor(
    private val glimRepository: GlimRepository,
) {
    operator fun invoke() = glimRepository.getGlimData()
}
