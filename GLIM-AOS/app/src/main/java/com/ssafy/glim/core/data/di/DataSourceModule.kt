package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {
    @Singleton
    @Binds
    fun bindAuthRemoteDataSource(dataSource: AuthRemoteDataSource): AuthRemoteDataSource

    @Singleton
    @Binds
    fun bindQuoteRemoteDataSource(dataSource: QuoteRemoteDataSource): QuoteRemoteDataSource
}