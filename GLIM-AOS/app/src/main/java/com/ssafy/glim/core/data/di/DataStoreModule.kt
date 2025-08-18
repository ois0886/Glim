package com.ssafy.glim.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ssafy.glim.app.deviceDataStore
import com.ssafy.glim.app.searchHistoryDataStore
import com.ssafy.glim.app.settingsDataStore
import com.ssafy.glim.core.data.datastore.DeviceDataStore
import com.ssafy.glim.core.data.datastore.SearchQueryDataStore
import com.ssafy.glim.core.data.datastore.SettingsDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataStoreModule {
    @Provides
    @Singleton
    @Named("search_history")
    fun provideSearchHistoryPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.searchHistoryDataStore
    }

    @Provides
    @Singleton
    fun provideSearchHistoryDataStore(
        @Named("search_history") dataStore: DataStore<Preferences>
    ): SearchQueryDataStore {
        return SearchQueryDataStore(dataStore)
    }

    @Provides
    @Singleton
    @Named("settings")
    fun provideSettingsPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.settingsDataStore
    }

    @Provides
    @Singleton
    fun provideSettingsDataStore(
        @Named("settings") dataStore: DataStore<Preferences>
    ): SettingsDataStore {
        return SettingsDataStore(dataStore)
    }

    @Provides
    @Singleton
    @Named("device")
    fun provideDevicePreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.deviceDataStore
    }

    @Provides
    @Singleton
    fun provideDeviceDataStore(
        @Named("device") dataStore: DataStore<Preferences>
    ): DeviceDataStore {
        return DeviceDataStore(dataStore)
    }
}
