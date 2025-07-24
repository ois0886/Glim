package com.ssafy.glim.core.domain.usecase.user

import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class DeleteUserUseCase @Inject constructor(
    private val repository: UserRepository,
) {
    suspend operator fun invoke(memberId: Long) = repository.deleteUser(memberId)
}
