package com.ssafy.glim.core.data.repository

import android.graphics.BitmapFactory
import com.ssafy.glim.core.data.datasource.remote.ImageRemoteDataSource
import com.ssafy.glim.core.data.dto.request.GenerateRequest
import com.ssafy.glim.core.domain.model.GeneratedImage
import com.ssafy.glim.core.domain.repository.ImageRepository
import javax.inject.Inject

class ImageRepositoryImpl @Inject constructor(
    private val imageRemoteDataSource: ImageRemoteDataSource
) : ImageRepository {
    override suspend fun generateImage(prompt: String) =
        imageRemoteDataSource.generateImage(GenerateRequest(prompt))
            .body().use { body ->
                body?.byteStream().use { stream ->
                    val bmp = BitmapFactory.decodeStream(stream)
                    GeneratedImage(bmp)
                }
            }
}
