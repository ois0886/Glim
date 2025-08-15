package com.ssafy.glim.core.domain.usecase.setting

import com.ssafy.glim.core.domain.repository.SettingsRepository
import jakarta.inject.Inject

class GetSettingsUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getCurrentSettings()
}
