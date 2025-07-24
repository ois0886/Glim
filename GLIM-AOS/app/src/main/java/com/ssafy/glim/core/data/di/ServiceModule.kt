package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.service.AuthService
import com.ssafy.glim.core.data.service.BookService
import com.ssafy.glim.core.data.service.CurationService
import com.ssafy.glim.core.data.service.QuoteService
import com.ssafy.glim.core.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideQuoteService(
        retrofit: Retrofit,
    ): QuoteService = retrofit.create(QuoteService::class.java)

    @Provides
    @Singleton
    fun provideBookService(
        retrofit: Retrofit,
    ): BookService = retrofit.create(BookService::class.java)

    @Provides
    @Singleton
    fun provideCurationService(
        retrofit: Retrofit,
    ): CurationService = retrofit.create(CurationService::class.java)

    @Provides
    @Singleton
    fun provideUserService(retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)
}
