package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.util.CaptureActions
import com.ssafy.glim.feature.post.PostState
import com.ssafy.glim.feature.post.component.editabletext.EditableTextField
import com.ssafy.glim.feature.post.component.editabletext.TextConfigContent
import com.ssafy.glim.feature.post.component.transformable.ImageTransformStateHolder
import com.ssafy.glim.feature.post.component.transformable.TransformableImage
import com.ssafy.glim.feature.post.component.transformable.rememberImageTransformState

@Composable
fun PostContent(
    state: PostState,
    onTextChanged: (TextFieldValue) -> Unit,
    updateTextFocusChanged: (Boolean) -> Unit,
    onBackgroundClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    startCameraAction: (CameraType) -> Unit,
    onImageGenerateClick : () -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
    onConfirmExit: () -> Unit,
    onCancelExit: () -> Unit,
    updateBottomSheetState: (Boolean) -> Unit,
    updateFontFamily: (FontFamily) -> Unit,
    updateTextColor: (Color) -> Unit,
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
                Color.Black
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
            updateTextFocusChanged = updateTextFocusChanged,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDrag = onDrag,
        )

        if (state.isFocused && !state.isDragging) {
            TextConfigContent(
                textStyle = state.textStyle,
                onIncreaseFontSize = onIncreaseFontSize,
                onDecreaseFontSize = onDecreaseFontSize,
                onToggleBold = onToggleBold,
                onToggleItalic = onToggleItalic,
                updateFontFamily = updateFontFamily,
                updateTextColor = updateTextColor,
                modifier = Modifier.navigationBarsPadding().align(Alignment.BottomCenter)
            )
        } else {
            PostUI(
                state = state,
                onImageGenerateClick = onImageGenerateClick,
                startCameraAction = startCameraAction,
                onTextExtractionClick = onTextExtractionClick,
                onBackgroundImageClick = onBackgroundImageClick,
                updateTextFocusChanged = updateTextFocusChanged,
                onCompleteClick = onCompleteClick,
                onBackPress = onBackPress,
                updateBottomSheetState = updateBottomSheetState,
                selectedBook = selectedBook,
                focusManager = focusManager,
                imageGraphicsLayer = imageGraphicsLayer,
                modifier = Modifier
                    .fillMaxSize()
                    .navigationBarsPadding()
            )
        }
    }
}

@Composable
private fun PostCaptureContent(
    state: PostState,
    transformState: ImageTransformStateHolder,
    imageGraphicsLayer: GraphicsLayer,
    onTextChanged: (TextFieldValue) -> Unit,
    updateTextFocusChanged: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .navigationBarsPadding()
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
            updateTextFocusChanged = updateTextFocusChanged,
            onTextChange = onTextChanged,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDrag = onDrag,
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
