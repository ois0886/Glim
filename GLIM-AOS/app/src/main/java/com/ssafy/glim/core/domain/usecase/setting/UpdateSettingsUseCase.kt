package com.ssafy.glim.core.domain.usecase.setting

import com.ssafy.glim.core.domain.model.Settings
import com.ssafy.glim.core.domain.repository.SettingsRepository
import jakarta.inject.Inject

class UpdateSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend operator fun invoke(settings: Settings) = settingsRepository.updateSettings(settings)
}
