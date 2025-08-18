package com.ssafy.glim.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R

@Composable
fun GlimErrorLoader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(80.dp)
            .background(
                color = Color.Gray.copy(alpha = 0.1f),
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.Center)
                .size(24.dp),
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}
