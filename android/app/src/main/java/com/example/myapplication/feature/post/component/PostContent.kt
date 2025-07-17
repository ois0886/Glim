package com.example.myapplication.feature.post.component

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import coil.compose.AsyncImage
import com.example.myapplication.feature.post.TextStyleState
import com.example.myapplication.feature.post.component.editabletext.EditableTextField

@Composable
fun PostContent(
    recognizedText: String,
    textStyle: TextStyleState,
    backgroundImageUri: Uri?,
    showExitDialog: Boolean,
    isFocused: Boolean,
    isDragging: Boolean,
    offsetX: Float,
    offsetY: Float,
    onTextChanged: (String) -> Unit,
    onTextFocusChanged: (Boolean) -> Unit,
    onBackgroundClick: () -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onConfirmExit: () -> Unit,
    onCancelExit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onBackgroundClick()
                    focusManager.clearFocus()
                }
                .background(
                    brush =
                        Brush.linearGradient(
                            colors = listOf(Color(0x881C1B1F), Color(0xFF1C1B1F)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, Float.POSITIVE_INFINITY),
                        ),
                ),
    ) {
        AsyncImage(
            model = backgroundImageUri,
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        EditableTextField(
            text = recognizedText,
            textStyle = textStyle,
            isFocused = isFocused,
            isDragging = isDragging,
            offsetX = offsetX,
            offsetY = offsetY,
            onTextChange = onTextChanged,
            onFocusChanged = onTextFocusChanged,
            onDragStart = onDragStart,
            onDragEnd = onDragEnd,
            onDrag = onDrag,
            onIncreaseFontSize = onIncreaseFontSize,
            onDecreaseFontSize = onDecreaseFontSize,
            onToggleBold = onToggleBold,
            onToggleItalic = onToggleItalic,
            modifier = Modifier.align(Alignment.Center),
        )

        ActionButtons(
            onTextExtractionClick = onTextExtractionClick,
            onBackgroundImageButtonClick = onBackgroundImageClick,
            onCompleteClick = onCompleteClick,
            modifier = Modifier.align(Alignment.BottomEnd),
        )

        BookInfoSection(
            modifier = Modifier.align(Alignment.BottomStart),
        )

        if (showExitDialog) {
            ExitConfirmDialog(onCancelExit, onConfirmExit)
        }
    }
}
