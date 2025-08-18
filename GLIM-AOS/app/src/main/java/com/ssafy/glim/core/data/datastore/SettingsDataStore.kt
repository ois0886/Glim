package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ssafy.glim.core.domain.model.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        val LOCK_SCREEN_ENABLED = booleanPreferencesKey("lock_screen_enabled")
        val ALL_NOTIFICATIONS_ENABLED = booleanPreferencesKey("all_notifications_enabled")
    }

    val settings: StateFlow<Settings> = dataStore.data
        .map { preferences ->
            Settings(
                isShowGlimEnabled = preferences[LOCK_SCREEN_ENABLED] != false,
                allNotificationsEnabled = preferences[ALL_NOTIFICATIONS_ENABLED] != false
            )
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = runBlocking {
                dataStore.data.first().let { preferences ->
                    Settings(
                        isShowGlimEnabled = preferences[LOCK_SCREEN_ENABLED] != false,
                        allNotificationsEnabled = preferences[ALL_NOTIFICATIONS_ENABLED] != false
                    )
                }
            }
        )

    suspend fun updateSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[LOCK_SCREEN_ENABLED] = settings.isShowGlimEnabled
            preferences[ALL_NOTIFICATIONS_ENABLED] = settings.allNotificationsEnabled
        }
    }
}
