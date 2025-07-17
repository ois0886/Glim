package com.example.myapplication.feature.post.component.editabletext

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.feature.post.TextStyleState
import kotlin.math.roundToInt

@Composable
fun EditableTextField(
    text: String,
    textStyle: TextStyleState,
    isFocused: Boolean,
    isDragging: Boolean,
    offsetX: Float,
    offsetY: Float,
    onTextChange: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onDragStart: () -> Unit,
    onDragEnd: () -> Unit,
    onDrag: (Float, Float) -> Unit,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                .border(
                    width = 1.dp,
                    color =
                        when {
                            isDragging -> Color.Yellow
                            isFocused -> Color.White
                            else -> Color.Transparent
                        },
                )
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
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (isFocused && !isDragging) {
            TextConfigContent(
                textStyle = textStyle,
                onIncreaseFontSize = onIncreaseFontSize,
                onDecreaseFontSize = onDecreaseFontSize,
                onToggleBold = onToggleBold,
                onToggleItalic = onToggleItalic,
            )
        }
        TextField(
            value = text,
            onValueChange = onTextChange,
            textStyle =
                MaterialTheme.typography.bodyMedium.copy(
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    fontSize = textStyle.fontSizeUnit,
                    fontWeight = textStyle.fontWeight,
                    fontStyle = textStyle.fontStyle,
                ),
            colors =
                TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            readOnly = isDragging,
            modifier =
                Modifier
                    .onFocusChanged { focusState ->
                        onFocusChanged(focusState.isFocused)
                    },
        )
    }
}
