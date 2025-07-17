package com.example.myapplication.feature.post.component.editabletext

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun TextStyleControls(
    isBold: Boolean,
    isItalic: Boolean,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        IconButton(
            onClick = onToggleBold,
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
            onClick = onToggleItalic,
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
