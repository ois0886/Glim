package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.CurationApi
import javax.inject.Inject

class TempCurationRemoteDataSource @Inject constructor(
    private val curationApi: CurationApi
) {
    suspend fun fetchMainCurations() = curationApi.getMainCurations()
}
