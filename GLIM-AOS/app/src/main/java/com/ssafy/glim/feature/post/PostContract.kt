package com.ssafy.glim.feature.post

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.ui.theme.glimDefaultFont

sealed interface PostSideEffect {
    data object NavigateBack : PostSideEffect

    data class OpenCamera(val type: CameraType) : PostSideEffect

    data object OpenTextImagePicker : PostSideEffect

    data object OpenBackgroundImagePicker : PostSideEffect

    data class SaveGeneratedToCache(val bitmap: Bitmap) : PostSideEffect

    data class ShowToast(val message: String) : PostSideEffect
}

data class PostState(
    val recognizedText: TextFieldValue = TextFieldValue(""),
    val capturedTextExtractionImageUri: Uri? = null,
    val selectedImageUri: Uri? = null,
    val backgroundImageUri: Uri? = null,
    val showExitDialog: Boolean = false,
    val textStyle: TextStyleState = TextStyleState(),
    val textPosition: TextPosition = TextPosition(),
    val originalTextPosition: TextPosition = TextPosition(),
    val isFocused: Boolean = false,
    val isDragging: Boolean = false,
    val book: Book? = null,
    val showBottomSheet: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TextStyleState(
    val fontSize: Float = 16f,
    val textColor: Color = Color.White,
    val fontFamily: FontFamily = glimDefaultFont,
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
