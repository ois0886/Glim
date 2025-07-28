package com.ssafy.glim.core.domain.usecase.auth

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class LogOutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke() = repository.logOut()
}
