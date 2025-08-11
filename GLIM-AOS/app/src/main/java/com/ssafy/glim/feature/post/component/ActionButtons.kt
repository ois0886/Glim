package com.ssafy.glim.feature.post.component

import android.os.SystemClock
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.util.CaptureActions
import com.ssafy.glim.core.util.rememberCaptureActions
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoxScope.ActionButtons(
    isTextNotEmpty: Boolean,
    visibility: Boolean,
    startCameraAction: (CameraType) -> Unit,
    onImageGenerateClick: () -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageButtonClick: () -> Unit,
    onCreateTextClick: (Boolean) -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
    onVisibilityClick: () -> Unit,
    clearFocus: () -> Unit,
    onBackPress: () -> Unit,
    graphicsLayer: GraphicsLayer,
    modifier: Modifier = Modifier,
) {
    val captureAction = rememberCaptureActions(
        graphicsLayer = graphicsLayer,
        fileName = "Quote_${System.currentTimeMillis()}.jpg",
    )
    val coroutineScope = rememberCoroutineScope()
    val tooltipState = rememberTooltipState()
    var hasShownTip by remember { mutableStateOf(false) }

    LaunchedEffect(isTextNotEmpty) {
        if (isTextNotEmpty && !hasShownTip) {
            hasShownTip = true
            coroutineScope.launch { tooltipState.show() }
            coroutineScope.launch {
                kotlinx.coroutines.delay(10000L)
                tooltipState.dismiss()
            }
        } else if (!isTextNotEmpty) {
            tooltipState.dismiss()
            hasShownTip = false
        }
    }

    Column(
        modifier =
        modifier
            .fillMaxHeight()
            .padding(4.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            DarkGrayRoundedSurface(modifier = modifier.alpha(if (visibility) 1f else 0f)) {
                IconButton(
                    onClick = if (visibility) {
                        onBackPress
                    } else {
                        {}
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = null
                    )
                }
            }

            DarkGrayRoundedSurface(modifier = modifier) {
                IconButton(
                    onClick = onVisibilityClick
                ) {
                    Icon(
                        painter = painterResource(
                            if (visibility) R.drawable.ic_visibility else R.drawable.ic_ic_visibility_off
                        ),
                        contentDescription = null
                    )
                }
            }

            Spacer(Modifier.weight(1f))

            if (visibility) {
                DarkGrayRoundedSurface(modifier = modifier) {
                    TextButton(onClick = {
                        coroutineScope.launch {
                            clearFocus()
                            onCompleteClick(captureAction)
                        }
                    }) {
                        Text("완료", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (visibility) {
        Surface(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp).align(Alignment.CenterEnd),
            color = Color.DarkGray.copy(alpha = 0.6f),
            shape = RoundedCornerShape(12.dp),
        ) {
            Column(
                modifier = Modifier.padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.End
            ) {
                TooltipBox(
                    modifier = modifier,
                    positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                    tooltip = {
                        PlainTooltip { Text("AI를 통해\n텍스트에 어울리는\n이미지를 만들어 보세요") }
                    },
                    state = tooltipState
                ) {
                    ActionButton(
                        onClick = onImageGenerateClick,
                        iconRes = R.drawable.ic_landscape,
                        contentDescription = stringResource(R.string.image_generate),
                        enabled = isTextNotEmpty,
                        minClickIntervalMillis = 1000L
                    )
                }

                IconButtonWithPopupMenu(
                    startCameraAction = startCameraAction
                )

                ActionButton(
                    onClick = onTextExtractionClick,
                    iconRes = R.drawable.ic_recognize,
                    contentDescription = stringResource(R.string.recognize_text),
                )

                ActionButton(
                    onClick = onBackgroundImageButtonClick,
                    iconRes = R.drawable.ic_image,
                    contentDescription = stringResource(R.string.background_image),
                )

                ActionButton(
                    onClick = { onCreateTextClick(true) },
                    iconRes = R.drawable.ic_title,
                    contentDescription = stringResource(R.string.new_text),
                )
            }
        }
    }
}

@Composable
fun DarkGrayRoundedSurface(
    modifier: Modifier = Modifier,
    alpha: Float = 0.8f,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.padding(8.dp),
        color = Color.DarkGray.copy(alpha = alpha),
        shape = RoundedCornerShape(12.dp),
    ) {
        content()
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean = true,
    minClickIntervalMillis: Long = 0L
) {
    val actualOnClick =
        if (minClickIntervalMillis > 0L)
            rememberThrottledClick(onClick, minClickIntervalMillis)
        else
            onClick

    IconButton(
        onClick = actualOnClick,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
        )
    }
}

@Composable
fun IconButtonWithPopupMenu(
    startCameraAction: (CameraType) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        ActionButton(
            onClick = { expanded = !expanded },
            iconRes = R.drawable.ic_photo,
            contentDescription = stringResource(R.string.recognize_text),
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = (-56).dp, y = (-72).dp),
            containerColor = Color.DarkGray.copy(alpha = 0.8f)
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.shot_background_image)) },
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.ic_image), contentDescription = null)
                },
                onClick = {
                    startCameraAction(CameraType.BACKGROUND_IMAGE)
                    expanded = false
                },
                colors = MenuItemColors(
                    textColor = Color.White,
                    leadingIconColor = Color.White,
                    trailingIconColor = Color.White,
                    disabledTextColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray,
                    disabledTrailingIconColor = Color.Gray,
                )
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.shot_text_image)) },
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.ic_title), contentDescription = null)
                },
                onClick = {
                    startCameraAction(CameraType.TEXT_RECOGNITION_IMAGE)
                    expanded = false
                },
                colors = MenuItemColors(
                    textColor = Color.White,
                    leadingIconColor = Color.White,
                    trailingIconColor = Color.White,
                    disabledTextColor = Color.Gray,
                    disabledLeadingIconColor = Color.Gray,
                    disabledTrailingIconColor = Color.Gray,
                )
            )
        }
    }
}

@Composable
fun rememberThrottledClick(
    onClick: () -> Unit,
    minIntervalMillis: Long = 800L
): () -> Unit {
    var lastClickAt by remember { mutableLongStateOf(0L) }
    return {
        val now = SystemClock.elapsedRealtime()
        if (now - lastClickAt >= minIntervalMillis) {
            lastClickAt = now
            onClick()
        }
    }
}
