package com.ssafy.glim.feature.setting

import androidx.annotation.StringRes
import com.ssafy.glim.core.domain.model.LockSettings

data class SettingUiState(
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val lockSettings: LockSettings = LockSettings(),
    val isLoading: Boolean = false
)

data class NotificationSettings(
    val allNotificationsEnabled: Boolean = true,
    val doNotDisturbEnabled: Boolean = true,
    val doNotDisturbTimeEnabled: Boolean = false,
    val weeklyNotificationsEnabled: Boolean = false,
    val defaultTimeRange: String = "22:00 - 08:00",
    val weekdayTimeRange: String = "22:00 - 07:00",
    val weekendTimeRange: String = "23:00 - 09:00"
)

sealed interface SettingSideEffect {
    data class ShowError(@StringRes val messageRes: Int) : SettingSideEffect
}
