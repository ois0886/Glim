package com.ssafy.glim.app

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.HiltAndroidApp

// DataStore 확장 프로퍼티
val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history",
)

val Context.settingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "settings",
)

@HiltAndroidApp
class GlimApplication : Application()
