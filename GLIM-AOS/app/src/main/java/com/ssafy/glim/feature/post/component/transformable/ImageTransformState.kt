package com.ssafy.glim.feature.post.component.transformable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size

@Composable
fun rememberImageTransformState(): ImageTransformStateHolder {
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var containerSize by remember { mutableStateOf(Size.Zero) }
    var imageSize by remember { mutableStateOf(Size.Zero) }
    var initialScale by remember { mutableFloatStateOf(1f) }

    return remember {
        ImageTransformStateHolder(
            scale = { scale },
            setScale = { scale = it },
            offset = { offset },
            setOffset = { offset = it },
            containerSize = { containerSize },
            setContainerSize = { containerSize = it },
            imageSize = { imageSize },
            setImageSize = { imageSize = it },
            initialScale = { initialScale },
            setInitialScale = { initialScale = it }
        )
    }
}

class ImageTransformStateHolder(
    val scale: () -> Float,
    val setScale: (Float) -> Unit,
    val offset: () -> Offset,
    val setOffset: (Offset) -> Unit,
    val containerSize: () -> Size,
    val setContainerSize: (Size) -> Unit,
    val imageSize: () -> Size,
    val setImageSize: (Size) -> Unit,
    val initialScale: () -> Float,
    val setInitialScale: (Float) -> Unit
) {

    fun updateContainerSize(newSize: Size) {
        setContainerSize(newSize)
        if (imageSize() != Size.Zero) {
            resetTransform()
        }
    }

    fun updateImageSize(newSize: Size) {
        setImageSize(newSize)
        if (containerSize() != Size.Zero) {
            resetTransform()
        }
    }

    private fun resetTransform() {
        val newInitialScale = ImageTransformUtils.calculateInitialScale(
            containerSize(),
            imageSize()
        )
        setInitialScale(newInitialScale)
        setScale(newInitialScale)
        setOffset(Offset.Zero)
    }
}
