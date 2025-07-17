package com.ssafy.glim.core.domain.usecase.glim

import com.ssafy.glim.core.domain.model.GlimInput
import com.ssafy.glim.core.domain.repository.FakeGlimRepository
import javax.inject.Inject

class UploadGlimUseCase
    @Inject
    constructor(
        private val glimRepository: FakeGlimRepository,
    ) {
        operator fun invoke(data: GlimInput) = glimRepository.saveGlimData(data)
    }
