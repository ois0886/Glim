package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.ssafy.glim.core.domain.model.LockSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LOCK_SCREEN_ENABLED = booleanPreferencesKey("lock_screen_enabled")
        val SHOW_QUOTES = booleanPreferencesKey("show_quotes")
        val AUTO_LOCK_DELAY = intPreferencesKey("auto_lock_delay")
        val ALLOW_CAMERA_ACCESS = booleanPreferencesKey("allow_camera_access")
    }

    val lockSettings: Flow<LockSettings> = dataStore.data.map { preferences ->
        LockSettings(
            isEnabled = preferences[LOCK_SCREEN_ENABLED] ?: true,
            showQuotes = preferences[SHOW_QUOTES] ?: true,
            autoLockDelay = preferences[AUTO_LOCK_DELAY] ?: 0,
            allowCameraAccess = preferences[ALLOW_CAMERA_ACCESS] ?: true
        )
    }

    suspend fun updateLockScreenSettings(settings: LockSettings) {
        dataStore.edit { preferences ->
            preferences[LOCK_SCREEN_ENABLED] = settings.isEnabled
            preferences[SHOW_QUOTES] = settings.showQuotes
            preferences[AUTO_LOCK_DELAY] = settings.autoLockDelay
            preferences[ALLOW_CAMERA_ACCESS] = settings.allowCameraAccess
        }
    }
}
