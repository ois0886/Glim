package com.ssafy.glim.core.domain.usecase.fcm

import com.ssafy.glim.core.domain.repository.FcmRepository
import jakarta.inject.Inject

class RegisterTokenUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    operator fun invoke(
        deviceToken: String,
        deviceId: String
    ) = fcmRepository.registerToken(
        deviceToken = deviceToken,
        deviceId = deviceId
    )
}
