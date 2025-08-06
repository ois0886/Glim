package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.CameraType
import com.ssafy.glim.core.util.CaptureActions
import com.ssafy.glim.core.util.rememberCaptureActions

import kotlinx.coroutines.launch

@Composable
fun ActionButtons(
    startCameraAction: (CameraType) -> Unit,
    onTextExtractionClick: () -> Unit,
    onBackgroundImageButtonClick: () -> Unit,
    onCreateTextClick: (Boolean) -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
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
            IconButton(
                onClick = onBackPress
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_close),
                    contentDescription = null
                )
            }
            Spacer(Modifier.weight(1f))
            TextButton(onClick = {
                coroutineScope.launch {
                    clearFocus()
                    onCompleteClick(captureAction)
                }
            }) {
                Text("완료", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
            contentDescription = "새 텍스트",
        )

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ActionButton(
    onClick: () -> Unit,
    iconRes: Int,
    contentDescription: String,
    enabled: Boolean = true,
) {
    IconButton(
        onClick = onClick,
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
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.shot_background_image)) },
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.ic_image), contentDescription = null)
                },
                onClick = {
                    startCameraAction(CameraType.BACKGROUND_IMAGE)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text(stringResource(R.string.shot_text_image)) },
                leadingIcon = {
                    Icon(painter = painterResource(R.drawable.ic_title), contentDescription = null)
                },
                onClick = {
                    startCameraAction(CameraType.TEXT_RECOGNITION_IMAGE)
                    expanded = false
                }
            )
        }
    }
}
