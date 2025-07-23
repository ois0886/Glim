package com.ssafy.glim.core.domain.usecase.auth

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase
    @Inject
    constructor(
        private val repository: AuthRepository,
    ) {
        operator fun invoke(
            email : String,
            nickname: String,
            password: String,
            gender: String,
            birthDate: String
        ) = repository.signUp(
            email = email,
            nickname = nickname,
            password = password,
            gender= gender,
            birthDate = birthDate
        )
    }
