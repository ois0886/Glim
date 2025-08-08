package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.api.AuthApi
import com.ssafy.glim.core.data.api.BookApi
import com.ssafy.glim.core.data.api.CurationApi
import com.ssafy.glim.core.data.api.QuoteApi
import com.ssafy.glim.core.data.api.SearchQueryApi
import com.ssafy.glim.core.data.api.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideAuthApi(
        @Named("auth") retrofit: Retrofit,
    ): AuthApi = retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideQuoteApi(
        @Named("main") retrofit: Retrofit,
    ): QuoteApi = retrofit.create(QuoteApi::class.java)

    @Provides
    @Singleton
    fun provideBookApi(
        @Named("main") retrofit: Retrofit,
    ): BookApi = retrofit.create(BookApi::class.java)

    @Provides
    @Singleton
    fun provideCurationApi(
        @Named("main") retrofit: Retrofit,
    ): CurationApi = retrofit.create(CurationApi::class.java)

    @Provides
    @Singleton
    fun provideUserApi(
        @Named("main") retrofit: Retrofit
    ): UserApi = retrofit.create(UserApi::class.java)

    @Provides
    @Singleton
    fun provideSearchQueryApi(
        @Named("main") retrofit: Retrofit
    ): SearchQueryApi = retrofit.create(SearchQueryApi::class.java)
}
