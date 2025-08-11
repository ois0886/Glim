package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.GenerateRequest
import com.ssafy.glim.core.data.service.ImageService
import javax.inject.Inject

class ImageRemoteDataSource @Inject constructor(
    private val imageService: ImageService
) {
    suspend fun generateImage(
        request: GenerateRequest,
    ) = imageService.generateImage(request)
}
