package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datastore.SettingsDataStore
import com.ssafy.glim.core.domain.model.Settings
import com.ssafy.glim.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val settingsDataStore: SettingsDataStore
) : SettingsRepository {

    // StateFlow의 현재 값을 즉시 반환
    override fun getCurrentSettings(): Settings {
        return settingsDataStore.settings.value
    }

    override fun getSettingsFlow(): Flow<Settings> {
        return settingsDataStore.settings
    }

    override suspend fun updateSettings(settings: Settings) {
        settingsDataStore.updateSettings(settings)
    }
}
