package com.ssafy.glim.core.domain.usecase.fcm

import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val fcmRepository: AuthRepository
) {
    suspend operator fun invoke(newToken: String) = fcmRepository.refreshFcmToken(newToken = newToken)
}
