package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.datasource.local.QuoteLocalDataSource
import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.SearchQueryRemoteDataSource
import com.ssafy.glim.core.data.api.AuthApi
import com.ssafy.glim.core.data.api.BookApi
import com.ssafy.glim.core.data.api.QuoteApi
import com.ssafy.glim.core.data.api.SearchQueryApi
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
    fun provideAuthRemoteDataSource(service: AuthApi) = AuthRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideBookRemoteDataSource(service: BookApi) = BookRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideQuoteRemoteDataSource(service: QuoteApi) = QuoteRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideSearchRemoteDataSource(service: SearchQueryApi) = SearchQueryRemoteDataSource(service)

    @Singleton
    @Provides
    fun provideQuoteLocalDataSource(): QuoteLocalDataSource = QuoteLocalDataSource()
}
