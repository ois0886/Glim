package com.example.myapplication.feature.post.component.editabletext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun TextSizeControls(
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = onDecreaseFontSize,
            modifier = Modifier.size(32.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_image),
                contentDescription = "텍스트 크기 줄이기",
                tint = Color.White,
                modifier = Modifier.size(16.dp),
            )
        }

        IconButton(
            onClick = onIncreaseFontSize,
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
