package com.ssafy.glim.feature.post.component.transformable

import androidx.compose.ui.geometry.Size

object ImageTransformUtils {

    /**
     * 이미지를 화면에 꽉 차게 하는 초기 스케일 계산
     */
    fun calculateInitialScale(containerSize: Size, imageSize: Size): Float {
        if (containerSize.width == 0f || containerSize.height == 0f ||
            imageSize.width == 0f || imageSize.height == 0f
        ) {
            return 1f
        }

        val containerRatio = containerSize.width / containerSize.height
        val imageRatio = imageSize.width / imageSize.height

        return if (imageRatio > containerRatio) {
            containerSize.height / imageSize.height
        } else {
            containerSize.width / imageSize.width
        }
    }

    /**
     * 이미지 이동 경계 계산
     */
    fun calculateBounds(
        containerSize: Size,
        imageSize: Size,
        currentScale: Float
    ): Pair<Float, Float> {
        val scaledImageWidth = imageSize.width * currentScale
        val scaledImageHeight = imageSize.height * currentScale

        val maxX = maxOf(0f, (scaledImageWidth - containerSize.width) / 2)
        val maxY = maxOf(0f, (scaledImageHeight - containerSize.height) / 2)

        return Pair(maxX, maxY)
    }
}
