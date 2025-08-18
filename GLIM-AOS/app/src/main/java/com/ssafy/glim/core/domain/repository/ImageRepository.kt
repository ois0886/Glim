package com.ssafy.glim.core.domain.repository

import com.ssafy.glim.core.domain.model.GeneratedImage

interface ImageRepository {

    suspend fun generateImage(
        prompt: String
    ): GeneratedImage
}
