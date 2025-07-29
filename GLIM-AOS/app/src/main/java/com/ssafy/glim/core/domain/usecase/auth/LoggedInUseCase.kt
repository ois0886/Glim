package com.ssafy.glim.core.domain.usecase.auth

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class LoggedInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.loggedIn()
}
