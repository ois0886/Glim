package com.ssafy.glim.core.domain.usecase.auth

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class CertifyValidCodeUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        operator fun invoke(code: String) = repository.sendVerificationCode(code)
    }
