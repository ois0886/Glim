package com.ssafy.glim.core.domain.model

data class LockSettings(
    val isEnabled: Boolean = true,
    val showQuotes: Boolean = true,
    val autoLockDelay: Int = 0,
    val allowCameraAccess: Boolean = true
)
