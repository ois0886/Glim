package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.GenerateRequest
import com.ssafy.glim.core.data.api.ImageApi
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(
    private val imageApi: ImageApi
) {
    suspend fun generateImage(
        request: GenerateRequest,
    ) = imageApi.generateImage(request)
}
