package com.ssafy.glim.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.glim.core.service.setting.LockServiceManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// DataStore 확장 프로퍼티
val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history",
)

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings",
)

val Context.deviceDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "device",
)

@HiltAndroidApp
class GlimApplication : Application()
