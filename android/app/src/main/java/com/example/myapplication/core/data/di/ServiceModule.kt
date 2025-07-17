package com.example.myapplication.core.data.di

import com.example.myapplication.core.data.remote.service.AuthService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Named
import jakarta.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @Named("default") retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)
}
