package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.data.service.AuthService
import com.ssafy.glim.core.data.service.BookService
import com.ssafy.glim.core.data.service.QuoteService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataSourceModule {

    @Singleton
    @Provides
    fun provideAuthRemoteDataSource(service: AuthService) = AuthRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideBookRemoteDataSource(service: BookService) = BookRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideQuoteRemoteDataSource(service: QuoteService) = QuoteRemoteDataSource(service)
}