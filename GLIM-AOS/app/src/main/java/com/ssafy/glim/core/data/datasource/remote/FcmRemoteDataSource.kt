package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.FcmApi
import com.ssafy.glim.core.data.dto.request.FcmRequest
import javax.inject.Inject

class FcmRemoteDataSource @Inject constructor(
    private val fcmApi: FcmApi
) {
    suspend fun sendToken(
        fcmRequest: FcmRequest
    ) = fcmApi.sendToken(request = fcmRequest)

    suspend fun deleteToken(
        deviceId: String
    ) = fcmApi.deleteToken(
        deviceId = deviceId
    )
}
