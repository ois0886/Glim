package com.ssafy.glim.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.ssafy.glim.app.searchHistoryDataStore
import com.ssafy.glim.core.data.datastore.SearchQueryDataStore
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
}
