package com.ssafy.glim.feature.auth.signup.component

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
import com.ssafy.glim.R
import com.ssafy.glim.feature.auth.login.component.EmailInputTextField
import com.ssafy.glim.feature.auth.login.component.PasswordInputTextField

@Composable
fun AuthInputContent(
    modifier: Modifier = Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: String? = null,
    password: String,
    onPasswordChange: (String) -> Unit,
    confirmPassword: String,
    onConfirmPasswordChange: (String) -> Unit,
    passwordError: String? = null,
    confirmPasswordError: String? = null,
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
            value = email,
            onValueChange = onEmailChange,
            error = emailError,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.password_instruction),
            style =
            MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordInputTextField(
            value = password,
            onValueChange = onPasswordChange,
            error = passwordError,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PasswordInputTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            error = confirmPasswordError,
            label = stringResource(R.string.password_confirm),
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.password_hint),
            style =
            MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                fontSize = 12.sp,
            ),
        )
    }
}

@Preview(name = "PasswordConfirmInputContent - Default", showBackground = true)
@Composable
fun PreviewPasswordConfirmInputContent_Default() {
    AuthInputContent(
        email = "",
        onEmailChange = {},
        emailError = null,
        password = "",
        onPasswordChange = {},
        confirmPassword = "",
        onConfirmPasswordChange = {},
        passwordError = null,
        confirmPasswordError = null,
    )
}

@Preview(name = "PasswordConfirmInputContent - With Errors", showBackground = true)
@Composable
fun PreviewPasswordConfirmInputContent_Errors() {
    AuthInputContent(
        email = "superoh",
        onEmailChange = {},
        emailError = "이메일 형식에 일치하지 않습니다.",
        password = "123",
        onPasswordChange = {},
        confirmPassword = "1234",
        onConfirmPasswordChange = {},
        passwordError = "8~16자, 대소문자/숫자/특수문자 포함",
        confirmPasswordError = "비밀번호가 일치하지 않습니다.",
    )
}
