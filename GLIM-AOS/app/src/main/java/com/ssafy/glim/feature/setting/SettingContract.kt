package com.ssafy.glim.feature.setting

import androidx.annotation.StringRes

data class SettingUiState(
    val notificationSettings: NotificationSettings = NotificationSettings(),
    val lockScreenSettings: LockScreenSettings = LockScreenSettings(),
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

data class LockScreenSettings(
    val glimEnabled: Boolean = true
)

sealed interface SettingSideEffect {
    data class ShowToast(@StringRes val messageRes: Int) : SettingSideEffect
    data class ShowError(@StringRes val messageRes: Int) : SettingSideEffect
}
