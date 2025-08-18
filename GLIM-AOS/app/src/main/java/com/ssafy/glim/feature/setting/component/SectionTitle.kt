package com.ssafy.glim.feature.setting.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.glim.ui.theme.Typography

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = Typography.bodyLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
    )
}
