package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.util.ShareManager
import com.ssafy.glim.core.data.api.ShortLinkApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class BitlyRetrofit

@Module
@InstallIn(SingletonComponent::class)
object ShortLinkModule {

    @Provides
    @Singleton
    @BitlyRetrofit
    fun provideBitlyRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api-ssl.bitly.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideShortLinkApi(@BitlyRetrofit retrofit: Retrofit): ShortLinkApi {
        return retrofit.create(ShortLinkApi::class.java)
    }

    @Provides
    @Singleton
    fun provideShareManger(): ShareManager = ShareManager()

}
