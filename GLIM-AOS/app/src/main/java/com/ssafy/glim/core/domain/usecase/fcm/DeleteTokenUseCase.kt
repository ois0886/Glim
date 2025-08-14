package com.ssafy.glim.core.domain.usecase.fcm

import com.ssafy.glim.core.domain.repository.FcmRepository
import jakarta.inject.Inject

class DeleteTokenUseCase @Inject constructor(
    private val fcmRepository: FcmRepository
) {
    suspend operator fun invoke() = fcmRepository.deleteToken()
}
