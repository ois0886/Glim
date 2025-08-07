package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ssafy.glim.core.domain.model.LockSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LOCK_SCREEN_ENABLED = booleanPreferencesKey("lock_screen_enabled")
    }

    val lockSettings: Flow<LockSettings> = dataStore.data.map { preferences ->
        LockSettings(
            isShowGlimEnabled = preferences[LOCK_SCREEN_ENABLED] != false
        )
    }

    suspend fun updateLockScreenSettings(settings: LockSettings) {
        dataStore.edit { preferences ->
            preferences[LOCK_SCREEN_ENABLED] = settings.isShowGlimEnabled
        }
    }
}
