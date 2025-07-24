package com.ssafy.glim.feature.lock.component

import androidx.compose.animation.AnimatedVisibility

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Done
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.FractionalThreshold
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.ssafy.glim.R
import com.ssafy.glim.ui.theme.GlimColor.LightGray300
import kotlin.math.roundToInt

// 드래그 방향 지정
enum class SwipeDirection {
    LeftToRight,
    RightToLeft
}

@Composable
private fun SwipeIndicator(
    modifier: Modifier = Modifier,
    backgroundColor: Color,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Transparent)
    ) {
        Icon(
            imageVector = Icons.Rounded.Check,
            contentDescription = null,
            tint = backgroundColor,
            modifier = Modifier.size(24.dp),
        )
    }
}

@OptIn(ExperimentalWearMaterialApi::class)
@Composable
fun SwipeButton(
    isIcon: Boolean = false,
    modifier: Modifier = Modifier,
    text: String,
    isComplete: Boolean,
    swipeDirection: SwipeDirection = SwipeDirection.LeftToRight,
    doneImageVector: ImageVector = Icons.Rounded.Done,
    backgroundColor: Color = Color.Transparent,
    paintRes: Int = R.drawable.ic_favorite,
    onSwipe: () -> Unit,
) {
    // 화면 너비(px) 계산
    val screenWidthDp = LocalConfiguration.current.screenWidthDp.dp
    val maxWidthPx = with(LocalDensity.current) { screenWidthDp.toPx() }
    // 스와이프 방향에 따른 앵커 설정
    val anchors = when (swipeDirection) {
        SwipeDirection.LeftToRight -> mapOf(0f to 0, maxWidthPx to 1)
        SwipeDirection.RightToLeft -> mapOf(0f to 0, -maxWidthPx to 1)
    }
    val swipeableState = rememberSwipeableState(0)
    var swipeComplete by remember { mutableStateOf(false) }
    val alpha: Float by animateFloatAsState(
        targetValue = if (swipeComplete) 0f else 1f,
        animationSpec = tween(durationMillis = 300),
        label = ""
    )

    LaunchedEffect(swipeableState.currentValue) {
        if (swipeableState.currentValue == 1) {
            swipeComplete = true
            onSwipe()
        }
    }

    // 인디케이터의 시작 위치 (왼→오 or 오→왼)
    val indicatorAlignment = if (swipeDirection == SwipeDirection.LeftToRight) Alignment.CenterStart else Alignment.CenterEnd

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .clip(if (swipeDirection == SwipeDirection.RightToLeft) RoundedCornerShape(0, 40, 40, 0) else RoundedCornerShape(40, 0, 0, 40))
            .background(backgroundColor)
            .swipeable(
                state = swipeableState,
                anchors = anchors,
                thresholds = { _, _ -> FractionalThreshold(0.3f) },
                orientation = Orientation.Horizontal
            )
            .offset { IntOffset(swipeableState.offset.value.roundToInt(), 0) }
            .then(
                if (swipeComplete) {
                    Modifier.width(64.dp)
                } else {
                    Modifier.fillMaxWidth()
                }
            )
            .height(64.dp),
    ) {
        // 인디케이터
        SwipeIndicator(
            modifier = Modifier
                .align(indicatorAlignment)
                .alpha(alpha),
            backgroundColor = backgroundColor,
        )
        // 텍스트
        if (!isIcon) {
            Text(
                text = text,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(alpha)
                    .padding(horizontal = 40.dp)
            )
        } else {
            Icon(
                painter = painterResource(paintRes),
                contentDescription = "",
                tint = LightGray300,
            )
        }
        // 진행 인디케이터
        AnimatedVisibility(visible = swipeComplete && !isComplete) {
            CircularProgressIndicator(
                color = Color.Transparent,
                strokeWidth = 1.dp,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp)
            )
        }
        // 완료 아이콘
        AnimatedVisibility(visible = isComplete) {
            Icon(
                imageVector = doneImageVector,
                contentDescription = null,
                tint = Color.Transparent,
                modifier = Modifier.size(44.dp),
            )
        }
    }
}
