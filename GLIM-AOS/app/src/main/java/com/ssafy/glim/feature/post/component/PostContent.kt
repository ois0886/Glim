package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.util.CaptureActions
import com.ssafy.glim.feature.post.PostState
import com.ssafy.glim.feature.post.component.transformable.TransformableImage
import com.ssafy.glim.feature.post.component.editabletext.EditableTextField
import com.ssafy.glim.feature.post.component.transformable.ImageTransformStateHolder
import com.ssafy.glim.feature.post.component.transformable.rememberImageTransformState

@Composable
fun PostContent(
    state: PostState,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFocusChanged: (Boolean) -> Unit,
    onBackgroundClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    startCameraAction: (CameraType) -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
    onConfirmExit: () -> Unit,
    onCancelExit: () -> Unit,
    updateBottomSheetState: (Boolean) -> Unit,
    selectedBook: (Book) -> Unit,
    onBackPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val transformState = rememberImageTransformState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onBackgroundClick()
                focusManager.clearFocus()
            }
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
                    start = Offset(0f, 0f),
                    end = Offset(0f, Float.POSITIVE_INFINITY)
                )
            )
    ) {
        if (state.showExitDialog) {
            ExitConfirmDialog(onCancelExit, onConfirmExit)
        }

        val imageGraphicsLayer = rememberGraphicsLayer()

        PostCaptureContent(
            state = state,
            transformState = transformState,
            imageGraphicsLayer = imageGraphicsLayer,
            onTextChanged = onTextChanged,
            onTextFocusChanged = onTextFocusChanged,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDrag = onDrag,
            onIncreaseFontSize = onIncreaseFontSize,
            onDecreaseFontSize = onDecreaseFontSize,
            onToggleBold = onToggleBold,
            onToggleItalic = onToggleItalic
        )

        PostUI(
            state = state,
            startCameraAction = startCameraAction,
            onTextExtractionClick = onTextExtractionClick,
            onBackgroundImageClick = onBackgroundImageClick,
            onTextFocusChanged = onTextFocusChanged,
            onCompleteClick = onCompleteClick,
            onBackPress = onBackPress,
            updateBottomSheetState = updateBottomSheetState,
            selectedBook = selectedBook,
            focusManager = focusManager,
            imageGraphicsLayer = imageGraphicsLayer,
            modifier = Modifier.fillMaxSize().navigationBarsPadding()
        )
    }
}

@Composable
private fun PostCaptureContent(
    state: PostState,
    transformState: ImageTransformStateHolder,
    imageGraphicsLayer: GraphicsLayer,
    onTextChanged: (TextFieldValue) -> Unit,
    onTextFocusChanged: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                onDrawWithContent {
                    imageGraphicsLayer.record {
                        this@onDrawWithContent.drawContent()
                    }
                    drawLayer(imageGraphicsLayer)
                }
            }
    ) {
        TransformableImage(
            imageUri = state.backgroundImageUri,
            transformState = transformState
        )

        EditableTextField(
            text = state.recognizedText,
            textStyle = state.textStyle,
            isFocused = state.isFocused,
            isDragging = state.isDragging,
            offsetX = state.textPosition.offsetX,
            offsetY = state.textPosition.offsetY,
            onTextChange = onTextChanged,
            onFocusChanged = onTextFocusChanged,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDrag = onDrag,
            onIncreaseFontSize = onIncreaseFontSize,
            onDecreaseFontSize = onDecreaseFontSize,
            onToggleBold = onToggleBold,
            onToggleItalic = onToggleItalic,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
