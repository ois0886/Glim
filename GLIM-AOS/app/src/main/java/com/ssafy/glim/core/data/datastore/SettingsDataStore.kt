package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
        val DO_NOT_DISTURB_ENABLED = booleanPreferencesKey("do_not_disturb_enabled")
        val DO_NOT_DISTURB_TIME_ENABLED = booleanPreferencesKey("do_not_disturb_time_enabled")
        val WEEKLY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("weekly_notifications_enabled")
        val DEFAULT_TIME_RANGE = stringPreferencesKey("default_time_range")
        val WEEKDAY_TIME_RANGE = stringPreferencesKey("weekday_time_range")
        val WEEKEND_TIME_RANGE = stringPreferencesKey("weekend_time_range")
    }

    val settings: StateFlow<Settings> = dataStore.data
        .map { preferences ->
            Settings(
                isShowGlimEnabled = preferences[LOCK_SCREEN_ENABLED] != false,
                allNotificationsEnabled = preferences[ALL_NOTIFICATIONS_ENABLED] != false,
                doNotDisturbEnabled = preferences[DO_NOT_DISTURB_ENABLED] != false,
                doNotDisturbTimeEnabled = preferences[DO_NOT_DISTURB_TIME_ENABLED] ?: false,
                weeklyNotificationsEnabled = preferences[WEEKLY_NOTIFICATIONS_ENABLED] ?: false,
                defaultTimeRange = preferences[DEFAULT_TIME_RANGE] ?: "22:00 - 08:00",
                weekdayTimeRange = preferences[WEEKDAY_TIME_RANGE] ?: "22:00 - 07:00",
                weekendTimeRange = preferences[WEEKEND_TIME_RANGE] ?: "23:00 - 09:00"
            )
        }
        .stateIn(
            scope = CoroutineScope(Dispatchers.IO + SupervisorJob()),
            started = SharingStarted.Eagerly,
            initialValue = runBlocking {
                dataStore.data.first().let { preferences ->
                    Settings(
                        isShowGlimEnabled = preferences[LOCK_SCREEN_ENABLED] != false,
                        allNotificationsEnabled = preferences[ALL_NOTIFICATIONS_ENABLED] != false,
                        doNotDisturbEnabled = preferences[DO_NOT_DISTURB_ENABLED] != false,
                        doNotDisturbTimeEnabled = preferences[DO_NOT_DISTURB_TIME_ENABLED] ?: false,
                        weeklyNotificationsEnabled = preferences[WEEKLY_NOTIFICATIONS_ENABLED] ?: false,
                        defaultTimeRange = preferences[DEFAULT_TIME_RANGE] ?: "22:00 - 08:00",
                        weekdayTimeRange = preferences[WEEKDAY_TIME_RANGE] ?: "22:00 - 07:00",
                        weekendTimeRange = preferences[WEEKEND_TIME_RANGE] ?: "23:00 - 09:00"
                    )
                }
            }
        )

    suspend fun updateSettings(settings: Settings) {
        dataStore.edit { preferences ->
            preferences[LOCK_SCREEN_ENABLED] = settings.isShowGlimEnabled
            preferences[ALL_NOTIFICATIONS_ENABLED] = settings.allNotificationsEnabled
            preferences[DO_NOT_DISTURB_ENABLED] = settings.doNotDisturbEnabled
            preferences[DO_NOT_DISTURB_TIME_ENABLED] = settings.doNotDisturbTimeEnabled
            preferences[WEEKLY_NOTIFICATIONS_ENABLED] = settings.weeklyNotificationsEnabled
            preferences[DEFAULT_TIME_RANGE] = settings.defaultTimeRange
            preferences[WEEKDAY_TIME_RANGE] = settings.weekdayTimeRange
            preferences[WEEKEND_TIME_RANGE] = settings.weekendTimeRange
        }
    }
}
