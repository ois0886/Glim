package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.service.AuthService
import com.ssafy.glim.core.data.service.QuoteService
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

    @Provides
    @Singleton
    fun provideQuoteService(
        retrofit: Retrofit,
    ): QuoteService = retrofit.create(QuoteService::class.java)
}
