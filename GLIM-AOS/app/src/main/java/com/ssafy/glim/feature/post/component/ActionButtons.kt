package com.ssafy.glim.feature.post.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.reels.CaptureActions
import com.ssafy.glim.feature.reels.rememberCaptureActions
import kotlinx.coroutines.launch

@Composable
fun ActionButtons(
    onTextExtractionClick: () -> Unit,
    onBackgroundImageButtonClick: () -> Unit,
    onCreateTextClick: (Boolean) -> Unit,
    onCompleteClick: (CaptureActions) -> Unit,
    clearFocus: () -> Unit,
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
            .padding(vertical = 16.dp, horizontal = 8.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextButton(onClick = {
            coroutineScope.launch {
                clearFocus()
                onCompleteClick(captureAction)
            }
        }) {
            Text("완료", color = Color.White, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.weight(1f))

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

        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun ActionButton(
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
