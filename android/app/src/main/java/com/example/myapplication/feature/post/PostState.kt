package com.example.myapplication.feature.post

import android.net.Uri
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

sealed interface PostSideEffect {
    data object NavigateBack : PostSideEffect

    data object OpenTextImagePicker : PostSideEffect

    data object OpenBackgroundImagePicker : PostSideEffect

    data class ShowToast(val message: String) : PostSideEffect
}

data class PostState(
    val recognizedText: String = "",
    val selectedImageUri: Uri? = null,
    val backgroundImageUri: Uri? = null,
    val showExitDialog: Boolean = false,
    val textStyle: TextStyleState = TextStyleState(),
    val textPosition: TextPosition = TextPosition(),
    val isFocused: Boolean = false,
    val isDragging: Boolean = false,
    val bookId: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TextStyleState(
    val fontSize: Float = 16f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
) {
    val fontSizeUnit: TextUnit get() = fontSize.sp
    val fontWeight: FontWeight get() = if (isBold) FontWeight.Bold else FontWeight.Normal
    val fontStyle: FontStyle get() = if (isItalic) FontStyle.Italic else FontStyle.Normal
}

data class TextPosition(
    val offsetX: Float = 0f,
    val offsetY: Float = 0f,
)
