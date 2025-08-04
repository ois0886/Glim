package com.ssafy.glim.feature.post.component.imageoverlay

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R

// 상수 정의
private object BottomControlConstants {
    const val BACKGROUND_ALPHA = 0.8f
    val PADDING = 8.dp
    val HORIZONTAL_PADDING = 16.dp
    val ICON_SPACING = 8.dp

    // 브러시 크기 관련
    const val MIN_BRUSH_SIZE = 5f
    const val MAX_BRUSH_SIZE = 30f

    // 두께 표시 원 크기
    val MIN_INDICATOR_SIZE = 16.dp
    val MIN_DOT_SIZE = 4.dp
    val MAX_INDICATOR_SIZE = 24.dp
    val MAX_DOT_SIZE = 12.dp

    // 색상
    val BACKGROUND_COLOR = Color.Black
    val ICON_TINT = Color.White
    val INDICATOR_BACKGROUND = Color.Gray
    val INDICATOR_DOT = Color.White
    val SLIDER_THUMB = Color.White
    val SLIDER_ACTIVE_TRACK = Color.White
    val SLIDER_INACTIVE_TRACK = Color.Gray
}

@Composable
fun ImageOverlayBottomControls(
    brushRadius: Float,
    onBrushRadiusChange: (Float) -> Unit,
    onUndo: () -> Unit,
    onReset: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(BottomControlConstants.BACKGROUND_COLOR.copy(alpha = BottomControlConstants.BACKGROUND_ALPHA))
            .padding(BottomControlConstants.PADDING),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        UndoButton(onClick = onUndo)

        BrushSizeControl(
            brushRadius = brushRadius,
            onBrushRadiusChange = onBrushRadiusChange,
            modifier = Modifier.weight(1f)
        )

        ResetButton(onClick = onReset)
    }
}

@Composable
private fun UndoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_undo),
            contentDescription = stringResource(R.string.undo),
            tint = BottomControlConstants.ICON_TINT
        )
    }
}

@Composable
private fun ResetButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_delete),
            contentDescription = stringResource(R.string.reset),
            tint = BottomControlConstants.ICON_TINT
        )
    }
}

@Composable
private fun BrushSizeControl(
    brushRadius: Float,
    onBrushRadiusChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.padding(horizontal = BottomControlConstants.HORIZONTAL_PADDING),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(BottomControlConstants.ICON_SPACING)
    ) {
        MinThicknessIndicator()

        BrushSizeSlider(
            brushRadius = brushRadius,
            onBrushRadiusChange = onBrushRadiusChange,
            modifier = Modifier.weight(1f)
        )

        MaxThicknessIndicator()
    }
}

@Composable
private fun MinThicknessIndicator() {
    ThicknessIndicator(
        size = BottomControlConstants.MIN_INDICATOR_SIZE,
        dotSize = BottomControlConstants.MIN_DOT_SIZE
    )
}

@Composable
private fun MaxThicknessIndicator() {
    ThicknessIndicator(
        size = BottomControlConstants.MAX_INDICATOR_SIZE,
        dotSize = BottomControlConstants.MAX_DOT_SIZE
    )
}

@Composable
private fun ThicknessIndicator(
    size: androidx.compose.ui.unit.Dp,
    dotSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(size)
            .background(BottomControlConstants.INDICATOR_BACKGROUND, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .background(BottomControlConstants.INDICATOR_DOT, CircleShape)
        )
    }
}

@Composable
private fun BrushSizeSlider(
    brushRadius: Float,
    onBrushRadiusChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Slider(
        value = brushRadius,
        onValueChange = onBrushRadiusChange,
        valueRange = BottomControlConstants.MIN_BRUSH_SIZE..BottomControlConstants.MAX_BRUSH_SIZE,
        modifier = modifier,
        colors = SliderDefaults.colors(
            thumbColor = BottomControlConstants.SLIDER_THUMB,
            activeTrackColor = BottomControlConstants.SLIDER_ACTIVE_TRACK,
            inactiveTrackColor = BottomControlConstants.SLIDER_INACTIVE_TRACK,
            activeTickColor = Color.Transparent,    // 틱 마크 제거
            inactiveTickColor = Color.Transparent   // 틱 마크 제거
        )
    )
}
