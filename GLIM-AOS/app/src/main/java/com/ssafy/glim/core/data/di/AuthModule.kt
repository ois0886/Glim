package com.ssafy.glim.core.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ssafy.glim.app.searchHistoryDataStore
import com.ssafy.glim.core.data.api.AuthApi
import com.ssafy.glim.core.data.authmanager.AuthDataStore
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.authmanager.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.Authenticator
import javax.inject.Named
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthManger(
        authDataStore: AuthDataStore,
    ): AuthManager =
        AuthManager(authDataStore, CoroutineScope(SupervisorJob() + Dispatchers.Default))

    @Provides
    @Singleton
    fun provideAuthDataStore(
        @Named("auth_preferences") dataStore: DataStore<Preferences>
    ): AuthDataStore = AuthDataStore(dataStore)

    @Provides
    @Singleton
    @Named("auth_preferences")
    fun provideSearchHistoryPreferencesDataStore(
        @ApplicationContext context: Context,
    ): DataStore<Preferences> {
        return context.searchHistoryDataStore
    }

    @Provides
    @Singleton
    fun provideAuthenticator(
        authManager: AuthManager,
        authApi: AuthApi
    ): Authenticator = TokenAuthenticator(authManager, authApi)
}
