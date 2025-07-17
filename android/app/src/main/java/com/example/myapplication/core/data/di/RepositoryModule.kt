package com.example.myapplication.core.data.di

import com.example.myapplication.core.data.repository.AuthRepositoryImpl
import com.example.myapplication.core.data.repository.fake.FakeGlimRepositoryImpl
import com.example.myapplication.core.domain.repository.AuthRepository
import com.example.myapplication.core.domain.repository.FakeGlimRepository
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
