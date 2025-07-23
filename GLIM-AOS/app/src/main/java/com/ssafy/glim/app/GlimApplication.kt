package com.ssafy.glim.app

import android.app.Application
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.glim.core.receiver.ScreenReceiver
import dagger.hilt.android.HiltAndroidApp

// DataStore 확장 프로퍼티
val Context.searchHistoryDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "search_history"
)

@HiltAndroidApp
class GlimApplication : Application()
