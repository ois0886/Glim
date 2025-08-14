package com.ssafy.glim.core.domain.usecase.fcm

import com.ssafy.glim.core.domain.repository.FcmRepository
import javax.inject.Inject

class RefreshTokenUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke(newToken: String) = fcmRepository.refreshToken(deviceToken = newToken)
}
