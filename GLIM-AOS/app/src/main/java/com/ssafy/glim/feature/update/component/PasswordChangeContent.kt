package com.ssafy.glim.feature.update.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R
import com.ssafy.glim.feature.auth.login.component.PasswordInputTextField
import com.ssafy.glim.feature.update.UpdateInfoUiState

@Composable
fun PasswordChangeContent(
    state: UpdateInfoUiState,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit
) {
    Column {
        Text(
            text = stringResource(R.string.current_password_label),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        PasswordInputTextField(
            value = state.currentPassword,
            onValueChange = onCurrentPasswordChanged,
            error = state.currentPasswordError?.let { stringResource(it) },
            label = stringResource(R.string.current_password_hint),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 새 비밀번호 입력 섹션
        Text(
            text = stringResource(R.string.new_password_instruction),
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
            ),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        PasswordInputTextField(
            value = state.newPassword,
            onValueChange = onNewPasswordChanged,
            error = state.newPasswordError?.let { stringResource(it) },
            label = stringResource(R.string.new_password_label),
        )

        Spacer(modifier = Modifier.height(12.dp))

        PasswordInputTextField(
            value = state.confirmPassword,
            onValueChange = onConfirmPasswordChanged,
            error = state.confirmPasswordError?.let { stringResource(it) },
            label = stringResource(R.string.password_confirm),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.password_hint),
            style = MaterialTheme.typography.bodySmall.copy(
                color = Color.Gray,
                fontSize = 12.sp,
            ),
        )
    }
}
