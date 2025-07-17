package com.example.myapplication.feature.post.component

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun ActionButtons(
    onTextExtractionClick: () -> Unit,
    onBackgroundImageButtonClick: () -> Unit,
    onCompleteClick: () -> Unit,
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
        TextButton(onClick = onCompleteClick) {
            Text("완료")
        }

        Spacer(modifier = Modifier.weight(1f))

        ActionButton(
            onClick = onTextExtractionClick,
            iconRes = R.drawable.ic_recognize,
            contentDescription = "텍스트 인식",
        )

        ActionButton(
            onClick = onBackgroundImageButtonClick,
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
