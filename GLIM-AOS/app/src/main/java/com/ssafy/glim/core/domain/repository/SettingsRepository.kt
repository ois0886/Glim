package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.Settings
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    fun getCurrentSettings(): Settings
    fun getSettingsFlow(): Flow<Settings>
    suspend fun updateSettings(settings: Settings)
}
