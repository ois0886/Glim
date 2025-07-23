package com.ssafy.glim.core.domain.usecase.auth

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoginUseCase
@Inject
constructor(
    private val repository: AuthRepository,
) {
    operator fun invoke(
        email: String,
        password: String,
    ) =
        repository.login(email = email, password = password)
}
