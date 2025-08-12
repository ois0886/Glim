package com.ssafy.glim.core.data.di

import androidx.navigation.Navigator
import com.ssafy.glim.core.data.service.AuthService
import com.ssafy.glim.core.data.service.BookService
import com.ssafy.glim.core.data.service.CurationService
import com.ssafy.glim.core.data.service.ImageService
import com.ssafy.glim.core.data.service.QuoteService
import com.ssafy.glim.core.data.service.SearchQueryService
import com.ssafy.glim.core.data.service.UserService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ServiceModule {
    @Provides
    @Singleton
    fun provideAuthService(
        @Named("auth") retrofit: Retrofit,
    ): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideQuoteService(
        @Named("main") retrofit: Retrofit,
    ): QuoteService = retrofit.create(QuoteService::class.java)

    @Provides
    @Singleton
    fun provideBookService(
        @Named("main") retrofit: Retrofit,
    ): BookService = retrofit.create(BookService::class.java)

    @Provides
    @Singleton
    fun provideCurationService(
        @Named("main") retrofit: Retrofit,
    ): CurationService = retrofit.create(CurationService::class.java)

    @Provides
    @Singleton
    fun provideUserService(
        @Named("main") retrofit: Retrofit
    ): UserService = retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideSearchQueryService(
        @Named("main") retrofit: Retrofit
    ): SearchQueryService = retrofit.create(SearchQueryService::class.java)

    @Provides
    @Singleton
    fun provideImageService(
        @Named("main") retrofit: Retrofit,
    ): ImageService = retrofit.create(ImageService::class.java)
}
