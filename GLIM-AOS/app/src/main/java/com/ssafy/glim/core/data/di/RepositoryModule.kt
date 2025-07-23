package com.ssafy.glim.core.data.di

import com.ssafy.glim.core.data.repository.AuthRepositoryImpl
import com.ssafy.glim.core.data.repository.SearchQueryRepositoryImpl
import com.ssafy.glim.core.data.repository.fake.FakeBookRepositoryImpl
import com.ssafy.glim.core.data.repository.fake.FakeGlimRepositoryImpl
import com.ssafy.glim.core.data.repository.fake.FakeQuoteRepositoryImpl
import com.ssafy.glim.core.domain.repository.AuthRepository
import com.ssafy.glim.core.domain.repository.BookRepository
import com.ssafy.glim.core.domain.repository.GlimRepository
import com.ssafy.glim.core.domain.repository.QuoteRepository
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
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
    fun bindGlimRepository(repository: FakeGlimRepositoryImpl): GlimRepository

    @Singleton
    @Binds
    fun bindAuthRepository(repository: AuthRepositoryImpl): AuthRepository

    @Singleton
    @Binds
    fun bindBookRepository(repository: FakeBookRepositoryImpl): BookRepository

    @Singleton
    @Binds
    fun bindQuoteRepository(repository: FakeQuoteRepositoryImpl): QuoteRepository

    @Singleton
    @Binds
    fun bindSearchQueryRepository(repository: SearchQueryRepositoryImpl): SearchQueryRepository
}
