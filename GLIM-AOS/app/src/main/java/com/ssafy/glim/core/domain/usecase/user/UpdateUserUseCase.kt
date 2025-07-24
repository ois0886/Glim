package com.ssafy.glim.core.domain.usecase.user

import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: String,
    ) = repository.updateUser(memberId, password, nickname, gender, birthDate)
}
