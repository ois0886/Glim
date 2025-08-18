package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.local.QuoteLocalDataSource
import com.ssafy.glim.core.data.datasource.remote.CurationRemoteDataSource
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.repository.CurationRepository
import javax.inject.Inject

class CurationRepositoryImpl @Inject constructor(
    private val curationDataSource: CurationRemoteDataSource,
    private val quoteLocalDataSource: QuoteLocalDataSource
) : CurationRepository {

    override suspend fun getMainCurations() =
        runCatching { curationDataSource.fetchMainCurations().map { it.toDomain() } }
            .onSuccess {
                quoteLocalDataSource.addQuotes(
                    it.flatMap { curation ->
                        curation.contents.quote
                    }
                )
            }
            .getOrThrow()
}
