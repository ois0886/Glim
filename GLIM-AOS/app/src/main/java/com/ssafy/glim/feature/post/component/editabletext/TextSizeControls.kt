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
fun TextSizeControls(
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
) {
    IconButton(
        onClick = onDecreaseFontSize,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_minus),
            contentDescription = stringResource(R.string.text_size_down),
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
    }

    IconButton(
        onClick = onIncreaseFontSize,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_add),
            contentDescription = stringResource(R.string.text_size_down),
            tint = Color.White,
            modifier = Modifier.size(16.dp),
        )
    }

}
