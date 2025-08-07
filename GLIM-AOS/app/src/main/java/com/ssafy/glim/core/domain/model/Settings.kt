package com.ssafy.glim.core.domain.model

data class Settings(
    val isShowGlimEnabled: Boolean = true,
    val allNotificationsEnabled: Boolean = true,
    val doNotDisturbEnabled: Boolean = true,
    val doNotDisturbTimeEnabled: Boolean = false,
    val weeklyNotificationsEnabled: Boolean = false,
    val defaultTimeRange: String = "22:00 - 08:00",
    val weekdayTimeRange: String = "22:00 - 07:00",
    val weekendTimeRange: String = "23:00 - 09:00"
)
