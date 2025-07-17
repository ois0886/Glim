package com.example.myapplication.core.domain.usecase.glim

import com.example.myapplication.core.domain.model.GlimInput
import com.example.myapplication.core.domain.repository.FakeGlimRepository
import javax.inject.Inject

class UploadGlimUseCase
    @Inject
    constructor(
        private val glimRepository: FakeGlimRepository,
    ) {
        operator fun invoke(data: GlimInput) = glimRepository.saveGlimData(data)
    }
