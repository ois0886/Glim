package com.example.myapplication.core.domain.usecase.auth

import com.example.myapplication.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        operator fun invoke() = repository.login()
    }
