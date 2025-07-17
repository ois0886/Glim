package com.example.myapplication.feature.auth.login.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun PasswordInputTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    label: String = stringResource(id = R.string.login_password_placeholder),
    error: String? = null,
) {
    var isPasswordVisible by remember { mutableStateOf(false) }

    val labelText = error ?: label

    TextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        isError = error != null,
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        modifier =
            modifier
                .fillMaxWidth()
                .background(Color.Transparent),
        label = {
            Text(
                text = labelText,
                color =
                    if (error != null) {
                        MaterialTheme.colorScheme.error
                    } else {
                        Color.Gray
                    },
                fontSize = 14.sp,
            )
        },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                Icon(
                    imageVector = Icons.Filled.Visibility,
                    contentDescription = if (isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보기",
                )
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
fun PasswordInputTextFieldPreview() {
    var text by remember { mutableStateOf("") }

    PasswordInputTextField(
        value = text,
        onValueChange = { text = it },
        error = null,
    )
}
