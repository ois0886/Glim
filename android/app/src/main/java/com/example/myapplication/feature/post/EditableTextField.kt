package com.example.myapplication.feature.post

import PostIntent
import TextStyleState
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun EditableTextField(
    text: String,
    textStyle: TextStyleState,
    isFocused: Boolean,
    onIntent: (PostIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(48.dp)
                .border(
                    width = 0.2.dp,
                    color = if (isFocused) Color.White else Color.Transparent,
                ),
    ) {
        TextField(
            value = text,
            onValueChange = { onIntent(PostIntent.OnTextChanged(it)) },
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
            modifier =
                Modifier.onFocusChanged { focusState ->
                    onIntent(PostIntent.OnFocusChanged(focusState.isFocused))
                },
        )

        if (isFocused) {
            TextConfigContent(
                textStyle = textStyle,
                onIntent = onIntent,
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun TextConfigContent(
    textStyle: TextStyleState,
    onIntent: (PostIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = Color.Black.copy(alpha = 0.8f),
        shape = RoundedCornerShape(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextSizeControls(
                fontSize = textStyle.fontSize,
                onIntent = onIntent,
            )

            Spacer(modifier = Modifier.width(8.dp))

            TextStyleControls(
                isBold = textStyle.isBold,
                isItalic = textStyle.isItalic,
                onIntent = onIntent,
            )
        }
    }
}

@Composable
private fun TextSizeControls(
    fontSize: Float,
    onIntent: (PostIntent) -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = { onIntent(PostIntent.DecreaseFontSize) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "텍스트 크기 줄이기",
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }

        Text(
            text = "${fontSize.toInt()}",
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        IconButton(
            onClick = { onIntent(PostIntent.IncreaseFontSize) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.icon_post),
                contentDescription = "텍스트 크기 키우기",
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
private fun TextStyleControls(
    isBold: Boolean,
    isItalic: Boolean,
    onIntent: (PostIntent) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = { onIntent(PostIntent.ToggleBold) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_title),
                contentDescription = "굵게",
                tint = if (isBold) Color.Yellow else Color.White,
                modifier = Modifier.size(16.dp),
            )
        }

        IconButton(
            onClick = { onIntent(PostIntent.ToggleItalic) },
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_more),
                contentDescription = "기울이기",
                tint = if (isItalic) Color.Yellow else Color.White,
                modifier = Modifier.size(16.dp),
            )
        }
    }
}

@Composable
fun ActionButtons(
    isProcessing: Boolean,
    onIntent: (PostIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxHeight()
                .padding(vertical = 16.dp, horizontal = 8.dp)
                .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.End,
    ) {
        TextButton(onClick = { onIntent(PostIntent.OnCompleteClick) }) {
            Text("완료")
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            onClick = { onIntent(PostIntent.OnTextExtractionClick) },
            enabled = !isProcessing,
            iconRes = R.drawable.ic_recognize,
            contentDescription = "텍스트 인식",
        )

        ActionButton(
            onClick = { onIntent(PostIntent.OnBackgroundImageClick) },
            iconRes = R.drawable.ic_image,
            contentDescription = "배경 이미지",
        )

        ActionButton(
            onClick = { /* 새 텍스트 생성 */ },
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
