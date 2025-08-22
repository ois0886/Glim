package com.ssafy.glim.feature.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.login.component.EmailInputTextField

@Composable
fun EmailAuthInputContent(
    modifier: Modifier = Modifier,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    error: String? = null,
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.auth_greeting),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.email_auth_instruction),
            style = MaterialTheme.typography.labelLarge
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
        value = TextFieldValue(""),
        onValueChange = {},
        error = null,
    )
}

@Preview(name = "EmailAuthInputContent - With Error", showBackground = true)
@Composable
fun PreviewEmailAuthInputContent_WithError() {
    EmailAuthInputContent(
        value = TextFieldValue("invalid_email@"),
        onValueChange = {},
        error = "이메일 형식이 올바르지 않아요.",
    )
}
