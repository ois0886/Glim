package com.ssafy.glim.core.domain.usecase.setting

import com.ssafy.glim.core.domain.model.LockSettings
import com.ssafy.glim.core.domain.repository.SettingsRepository
import jakarta.inject.Inject

class UpdateLockSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: LockSettings) = settingsRepository.updateLockScreenSettings(settings)
}
