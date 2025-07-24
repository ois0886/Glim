package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.TempCurationRemoteDataSource
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.repository.CurationRepository
import javax.inject.Inject

class CurationRepositoryImpl @Inject constructor(
    private val curationDataSource: TempCurationRemoteDataSource
) : CurationRepository {
    override suspend fun getMainCurations() =
        curationDataSource.fetchMainCurations().map { it.toDomain() }
}
