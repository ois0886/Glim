package com.ssafy.glim.core.domain.usecase.user

import com.ssafy.glim.core.domain.repository.UserRepository
import javax.inject.Inject

class GetUserByEmailUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(email: String) = repository.getUserByEmail(email)
}
