package com.ssafy.glim.feature.setting

import androidx.annotation.StringRes
import com.ssafy.glim.core.domain.model.Settings

data class SettingUiState(
    val settings: Settings = Settings(),
    val isLoading: Boolean = false
)

sealed interface SettingSideEffect {
    data class ShowError(@StringRes val messageRes: Int) : SettingSideEffect
}
