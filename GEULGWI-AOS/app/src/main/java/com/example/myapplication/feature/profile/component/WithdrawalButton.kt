package com.example.myapplication.feature.profile.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
internal fun WithdrawalButton(
    onWithdrawalClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onWithdrawalClick,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.settings_withdrawal),
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalButton() {
    MaterialTheme {
        WithdrawalButton(
            onWithdrawalClick = {}
        )
    }
}