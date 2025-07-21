package com.ssafy.glim.feature.post.component.editabletext

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R

@Composable
fun TextStyleControls(
    isBold: Boolean,
    isItalic: Boolean,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
) {
    IconButton(
        onClick = onToggleBold,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_bold),
            contentDescription = stringResource(R.string.bold),
            tint = if (isBold) Color.Yellow else Color.White,
            modifier = Modifier.size(16.dp),
        )
    }

    IconButton(
        onClick = onToggleItalic,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_italic),
            contentDescription = stringResource(R.string.italic),
            tint = if (isItalic) Color.Yellow else Color.White,
            modifier = Modifier.size(16.dp),
        )
    }
}
