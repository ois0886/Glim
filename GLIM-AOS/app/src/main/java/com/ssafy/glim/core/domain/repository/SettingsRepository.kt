package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.LockSettings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getLockScreenSettings(): Flow<LockSettings>
    suspend fun updateLockScreenSettings(settings: LockSettings)
}
