package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.datasource.impl.QuoteRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
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