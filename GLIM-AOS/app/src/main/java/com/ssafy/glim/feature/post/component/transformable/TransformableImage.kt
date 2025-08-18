package com.ssafy.glim.feature.post.component.transformable

import android.net.Uri
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import coil.compose.AsyncImage

@Composable
fun TransformableImage(
    imageUri: Uri?,
    alpha: Float,
    transformState: ImageTransformStateHolder,
    modifier: Modifier = Modifier
) {
    val transformableState = rememberTransformableState { zoomChange, offsetChange, _ ->
        handleTransform(
            transformState = transformState,
            zoomChange = zoomChange,
            offsetChange = offsetChange
        )
    }

    AsyncImage(
        model = imageUri,
        contentDescription = null,
        modifier = modifier
            .fillMaxSize()
            .alpha(alpha)
            .onSizeChanged { size ->
                transformState.updateContainerSize(
                    Size(size.width.toFloat(), size.height.toFloat())
                )
            }
            .graphicsLayer(
                scaleX = transformState.scale(),
                scaleY = transformState.scale(),
                translationX = transformState.offset().x,
                translationY = transformState.offset().y
            )
            .transformable(state = transformableState),
        contentScale = ContentScale.Fit,
        onSuccess = { result ->
            val drawable = result.result.drawable
            transformState.updateImageSize(
                Size(
                    width = drawable.intrinsicWidth.toFloat(),
                    height = drawable.intrinsicHeight.toFloat()
                )
            )
        }
    )
}

private fun handleTransform(
    transformState: ImageTransformStateHolder,
    zoomChange: Float,
    offsetChange: Offset
) {
    val newScale = (transformState.scale() * zoomChange).coerceIn(
        transformState.initialScale(),
        maxOf(5f, transformState.initialScale())
    )

    val (maxX, maxY) = ImageTransformUtils.calculateBounds(
        containerSize = transformState.containerSize(),
        imageSize = transformState.imageSize(),
        currentScale = newScale
    )

    val scaleRatio = newScale / transformState.scale()
    val adjustedCurrentOffset = transformState.offset() * scaleRatio
    val speedAdjustedOffset = offsetChange * newScale

    transformState.setScale(newScale)
    transformState.setOffset(
        Offset(
            x = (adjustedCurrentOffset.x + speedAdjustedOffset.x).coerceIn(-maxX, maxX),
            y = (adjustedCurrentOffset.y + speedAdjustedOffset.y).coerceIn(-maxY, maxY)
        )
    )
}
