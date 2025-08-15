package com.ssafy.glim.core.domain.usecase.setting

import com.ssafy.glim.core.domain.repository.SettingsRepository
import javax.inject.Inject

class GetSettingsFlowUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    operator fun invoke() = settingsRepository.getSettingsFlow()
}
