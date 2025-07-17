package com.example.myapplication.core.domain.usecase.glim

import com.example.myapplication.core.domain.repository.FakeGlimRepository
import javax.inject.Inject

class GetGlimsUseCase
    @Inject
    constructor(
        private val glimRepository: FakeGlimRepository,
    ) {
        operator fun invoke() = glimRepository.getGlimData()
    }
