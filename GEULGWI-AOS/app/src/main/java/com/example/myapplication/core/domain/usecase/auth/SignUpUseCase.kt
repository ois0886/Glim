package com.example.myapplication.core.domain.usecase.auth

import com.example.myapplication.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        operator fun invoke() = repository.signUp()
    }
