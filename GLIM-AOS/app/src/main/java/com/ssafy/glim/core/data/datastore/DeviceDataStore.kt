package com.ssafy.glim.core.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.first
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeviceDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    companion object {
        private val DEVICE_ID_KEY = stringPreferencesKey("device_id")
        private val FCM_TOKEN_KEY = stringPreferencesKey("fcm_token")
    }

    suspend fun getDeviceId(): String {
        return dataStore.data.first()[DEVICE_ID_KEY] ?: run {
            val newDeviceId = "glim_${UUID.randomUUID()}"
            dataStore.edit { preferences ->
                preferences[DEVICE_ID_KEY] = newDeviceId
            }
            newDeviceId
        }
    }

    suspend fun saveFcmToken(fcmToken: String) {
        dataStore.edit { preferences ->
            preferences[FCM_TOKEN_KEY] = fcmToken
        }
    }

    suspend fun clearFcmToken() {
        dataStore.edit { preferences ->
            preferences.remove(FCM_TOKEN_KEY)
        }
    }
}
