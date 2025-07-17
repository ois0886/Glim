package com.example.myapplication.feature.auth.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R
import com.example.myapplication.feature.auth.login.component.EmailInputTextField

@Composable
fun EmailAuthInputContent(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    error: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.auth_greeting),
            style =
                MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray,
                    fontSize = 14.sp,
                ),
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.email_auth_instruction),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        EmailInputTextField(
            value = value,
            onValueChange = onValueChange,
            error = error,
        )
    }
}

@Preview(name = "EmailAuthInputContent - Empty", showBackground = true)
@Composable
fun PreviewEmailAuthInputContent_Empty() {
    EmailAuthInputContent(
        value = "",
        onValueChange = {},
        error = null,
    )
}

@Preview(name = "EmailAuthInputContent - With Error", showBackground = true)
@Composable
fun PreviewEmailAuthInputContent_WithError() {
    EmailAuthInputContent(
        value = "invalid_email@",
        onValueChange = {},
        error = "이메일 형식이 올바르지 않아요.",
    )
}
