package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datastore.SettingsDataStore
import com.ssafy.glim.core.domain.model.LockSettings
import com.ssafy.glim.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    override fun getLockScreenSettings(): Flow<LockSettings> {
        return settingsDataStore.lockSettings
    }

    override suspend fun updateLockScreenSettings(settings: LockSettings) {
        settingsDataStore.updateLockScreenSettings(settings)
    }
}
