package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.repository.AuthRepositoryImpl
import com.ssafy.glim.core.data.repository.BookRepositoryImpl
import com.ssafy.glim.core.data.repository.CurationRepositoryImpl
import com.ssafy.glim.core.data.repository.QuoteRepositoryImpl
import com.ssafy.glim.core.data.repository.SearchQueryRepositoryImpl
import com.ssafy.glim.core.data.repository.UserRepositoryImpl
import com.ssafy.glim.core.domain.repository.AuthRepository
import com.ssafy.glim.core.domain.repository.BookRepository
import com.ssafy.glim.core.domain.repository.CurationRepository
import com.ssafy.glim.core.domain.repository.QuoteRepository
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import com.ssafy.glim.core.domain.repository.UserRepository
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
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindBookRepository(repository: BookRepositoryImpl): BookRepository

    @Singleton
    @Binds
    fun bindQuoteRepository(repository: QuoteRepositoryImpl): QuoteRepository

    @Singleton
    @Binds
    fun bindSearchQueryRepository(repository: SearchQueryRepositoryImpl): SearchQueryRepository

    @Singleton
    @Binds
    fun bindUserRepository(repository: UserRepositoryImpl): UserRepository

    @Singleton
    @Binds
    fun bindCurationRepository(repository: CurationRepositoryImpl): CurationRepository
}
