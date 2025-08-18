package com.ssafy.glim.core.domain.usecase.image

import com.ssafy.glim.core.domain.repository.ImageRepository
import javax.inject.Inject

class ImageGenerateUseCase @Inject constructor(
    private val imageRepository: ImageRepository
) {
    suspend operator fun invoke(
        prompt: String
    ) = imageRepository.generateImage(prompt)
}
