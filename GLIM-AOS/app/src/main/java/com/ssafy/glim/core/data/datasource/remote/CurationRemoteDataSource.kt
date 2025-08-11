package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.service.CurationService
import javax.inject.Inject

class CurationRemoteDataSource @Inject constructor(
    private val curationService: CurationService
) {
    suspend fun fetchMainCurations() = curationService.getMainCurations()
}
