package com.ssafy.glim.core.domain.usecase.glim

import com.ssafy.glim.core.domain.model.GlimInput
import com.ssafy.glim.core.domain.repository.GlimRepository
import javax.inject.Inject

class UploadGlimUseCase
    @Inject
    constructor(
        private val glimRepository: GlimRepository,
    ) {
        operator fun invoke(data: GlimInput) = glimRepository.saveGlimData(data)
    }
