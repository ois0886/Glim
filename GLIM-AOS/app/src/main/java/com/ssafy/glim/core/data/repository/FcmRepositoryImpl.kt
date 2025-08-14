package com.ssafy.glim.core.data.repository

import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.glim.core.data.datasource.remote.FcmRemoteDataSource
import com.ssafy.glim.core.data.datastore.DeviceDataStore
import com.ssafy.glim.core.data.dto.request.FcmRequest
import com.ssafy.glim.core.domain.repository.FcmRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FcmRepositoryImpl @Inject constructor(
    private val fcmRemoteDataSource: FcmRemoteDataSource,
    private val deviceDataStore: DeviceDataStore
) : FcmRepository {

    override suspend fun registerToken(): Result<Unit> = runCatching {
        val fcmToken = FirebaseMessaging.getInstance().token.await()
        val deviceId = deviceDataStore.getDeviceId()

        deviceDataStore.saveFcmToken(fcmToken)
        fcmRemoteDataSource.sendToken(
            FcmRequest(
                deviceToken = fcmToken,
                deviceType = "ANDROID",
                deviceId = deviceId
            )
        )
    }

    override suspend fun refreshToken(newToken: String): Result<Unit> = runCatching {
        deviceDataStore.saveFcmToken(newToken)
    }

    override suspend fun deleteToken(): Result<Unit> = runCatching {
        deviceDataStore.clearFcmToken()
        fcmRemoteDataSource.deleteToken(deviceDataStore.getDeviceId())
    }
}
