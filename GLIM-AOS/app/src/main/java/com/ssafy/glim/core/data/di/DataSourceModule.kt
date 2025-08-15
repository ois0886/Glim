package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.api.AuthApi
import com.ssafy.glim.core.data.api.BookApi
import com.ssafy.glim.core.data.api.CurationApi
import com.ssafy.glim.core.data.api.FcmApi
import com.ssafy.glim.core.data.api.ImageApi
import com.ssafy.glim.core.data.api.QuoteApi
import com.ssafy.glim.core.data.api.SearchQueryApi
import com.ssafy.glim.core.data.api.UserApi
import com.ssafy.glim.core.data.datasource.local.QuoteLocalDataSource
import com.ssafy.glim.core.data.datasource.local.SearchQueryLocalDataSource
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.BookRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.CurationRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.FcmRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.ImageRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.QuoteRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.SearchQueryRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.UserRemoteDataSource
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
    fun provideAuthRemoteDataSource(api: AuthApi) = AuthRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideBookRemoteDataSource(api: BookApi) = BookRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideQuoteRemoteDataSource(api: QuoteApi) = QuoteRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideSearchRemoteDataSource(api: SearchQueryApi) = SearchQueryRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideCurationRemoteDataSource(api: CurationApi) = CurationRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideUserRemoteDataSource(api: UserApi) = UserRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideFcmRemoteDataSource(api: FcmApi) = FcmRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideImageRemoteDataSource(api: ImageApi): ImageRemoteDataSource = ImageRemoteDataSource(api)

    @Singleton
    @Provides
    fun provideQuoteLocalDataSource(): QuoteLocalDataSource = QuoteLocalDataSource()

    @Singleton
    @Provides
    fun provideSearchLocalDataSource(): SearchQueryLocalDataSource = SearchQueryLocalDataSource()
}
