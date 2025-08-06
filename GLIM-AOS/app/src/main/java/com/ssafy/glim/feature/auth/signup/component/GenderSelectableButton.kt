package com.ssafy.glim.feature.auth.signup.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GenderSelectableButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
            contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.primary,
            disabledContainerColor = if (isSelected) {
                MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            } else {
                Color.Transparent
            },
            disabledContentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = if (enabled) {
                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
            }
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
