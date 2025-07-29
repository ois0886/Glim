package com.ssafy.glim.core.domain.usecase.user

import com.ssafy.glim.core.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByIdUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke() = repository.getUserById()
}
