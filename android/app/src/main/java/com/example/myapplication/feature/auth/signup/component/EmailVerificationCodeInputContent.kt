package com.example.myapplication.feature.auth.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun EmailVerificationCodeInputContent(
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
            text = stringResource(R.string.verification_code_instruction),
            style =
                MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        TextField(
            value = value,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    onValueChange(input)
                }
            },
            modifier =
                modifier
                    .fillMaxWidth(),
            isError = error != null,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            label = {
                Text(
                    text = error ?: stringResource(id = R.string.verification_code_placeholder),
                    color =
                        if (error != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            Color.Gray
                        },
                    fontSize = 14.sp,
                )
            },
            singleLine = true,
        )
    }
}

@Preview(name = "Verification Code - Empty", showBackground = true)
@Composable
fun PreviewEmailVerificationCodeInputContent_Empty() {
    EmailVerificationCodeInputContent(
        value = "",
        onValueChange = {},
        error = null,
    )
}

@Preview(name = "Verification Code - With Error", showBackground = true)
@Composable
fun PreviewEmailVerificationCodeInputContent_WithError() {
    EmailVerificationCodeInputContent(
        value = "123",
        onValueChange = {},
        error = "인증번호가 올바르지 않아요",
    )
}
