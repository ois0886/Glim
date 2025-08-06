package com.ssafy.glim.feature.post.component.editabletext

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.sp
import com.ssafy.glim.feature.post.TextStyleState
import kotlin.math.roundToInt

@Composable
fun EditableTextField(
    text: TextFieldValue,
    textStyle: TextStyleState,
    isFocused: Boolean,
    isDragging: Boolean,
    offsetX: Float,
    offsetY: Float,
    updateTextFocusChanged: (Boolean) -> Unit,
    onTextChange: (TextFieldValue) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val focusRequester = remember { FocusRequester() }

    BackHandler(isFocused) {
        focusRequester.freeFocus()
        updateTextFocusChanged(false)
        focusManager.clearFocus()
    }

    LaunchedEffect(isFocused) {
        if (isFocused) {
            focusRequester.requestFocus()
        }
    }
    // 텍스트 핸들(커서) 색상, 선택 텍스트 배경색 지정
    val customSelectionColors = TextSelectionColors(
        handleColor = Color.White,
        backgroundColor = Color.White.copy(alpha = 0.3f)
    )

    CompositionLocalProvider(LocalTextSelectionColors provides customSelectionColors) {
        BasicTextField(
            value = text,
            onValueChange = onTextChange,
            textStyle =
            MaterialTheme.typography.bodyMedium.copy(
                textAlign = TextAlign.Center,
                lineHeight = 40.sp,
                fontSize = textStyle.fontSizeUnit,
                fontWeight = textStyle.fontWeight,
                fontStyle = textStyle.fontStyle,
                fontFamily = textStyle.fontFamily,
                color = textStyle.textColor
            ),
            readOnly = isDragging,
            cursorBrush = SolidColor(Color.White),
            modifier = modifier
                .focusRequester(focusRequester)
                .onFocusChanged { focusState ->
                    updateTextFocusChanged(focusState.isFocused)
                }
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            onDragStart()
                        },
                        onDragEnd = {
                            onDragEnd()
                        },
                    ) { _, dragAmount ->
                        onDrag(dragAmount.x, dragAmount.y)
                    }
                },
        )
    }
}
