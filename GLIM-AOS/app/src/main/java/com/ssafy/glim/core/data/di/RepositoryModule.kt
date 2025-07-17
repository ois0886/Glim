package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.repository.AuthRepositoryImpl
import com.ssafy.glim.core.data.repository.fake.FakeGlimRepositoryImpl
import com.ssafy.glim.core.domain.repository.AuthRepository
import com.ssafy.glim.core.domain.repository.FakeGlimRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Singleton
    @Binds
    fun bindGlimRepository(repository: FakeGlimRepositoryImpl): FakeGlimRepository

    @Singleton
    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository
}
