package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.FcmApi
import com.ssafy.glim.core.data.dto.request.FCMRequest
import javax.inject.Inject

class FcmRemoteDataSource @Inject constructor(
    private val fcmApi: FcmApi
) {
    suspend fun sendToken(
        fcmRequest: FCMRequest
    ) = fcmApi.sendToken(request = fcmRequest)
}
