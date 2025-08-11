package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.data.datasource.remote.FcmRemoteDataSource
import com.ssafy.glim.core.data.dto.request.FcmRequest
import com.ssafy.glim.core.domain.repository.FcmRepository
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val fcmRemoteDataSource: FcmRemoteDataSource
) : FcmRepository {

    override suspend fun registerToken(deviceToken: String, deviceType: String, deviceId: String) {
        fcmRemoteDataSource.sendToken(
            FcmRequest(
                deviceToken = deviceToken,
                deviceType = deviceType,
                deviceId = deviceId
            )
        )
    }

    override suspend fun deleteToken(deviceId: String) {
        fcmRemoteDataSource.deleteToken(deviceId = deviceId)
    }
}
