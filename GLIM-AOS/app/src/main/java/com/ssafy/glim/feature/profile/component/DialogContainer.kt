package com.ssafy.glim.feature.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R
import com.ssafy.glim.feature.profile.EditProfileDialogState
import com.ssafy.glim.feature.profile.LogoutDialogState
import com.ssafy.glim.feature.profile.ProfileUiState
import com.ssafy.glim.feature.profile.WithdrawalDialogState

@Composable
fun EditProfileDialogContainer(
    state: ProfileUiState,
    onPersonalInfoClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onCancel: () -> Unit
) {
    if (state.editProfileDialogState == EditProfileDialogState.Showing) {
        AlertDialog(
            containerColor = Color.White,
            onDismissRequest = onCancel,
            title = {
                Text(
                    text = stringResource(R.string.profile_edit_title),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            text = {
                Column {
                    Text(
                        text = stringResource(R.string.profile_edit_question),
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = onPersonalInfoClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.profile_edit_personal_info),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedButton(
                        onClick = onPasswordChangeClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Black
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.profile_edit_password),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Black,
                        contentColor = Color.White
                    )
                ) {
                    Text(stringResource(R.string.cancel))
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun LogoutDialogContainer(
    state: ProfileUiState,
    onLogoutConfirm: () -> Unit,
    onLogoutCancel: () -> Unit,
) {
    when (state.logoutDialogState) {
        LogoutDialogState.Confirmation -> {
            LogoutConfirmationDialog(
                onConfirm = onLogoutConfirm,
                onCancel = onLogoutCancel
            )
        }

        LogoutDialogState.Processing -> {
            LogoutProcessingDialog()
        }

        LogoutDialogState.Hidden -> Unit
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.logout_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.logout_message),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.logout_info_message),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.logout_confirm))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun LogoutProcessingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.logout_processing_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(R.string.logout_processing_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

@Composable
fun WithdrawalDialogContainer(
    state: ProfileUiState,
    onWarningConfirm: () -> Unit,
    onWarningCancel: () -> Unit,
    onUserInputChanged: (String) -> Unit,
    onFinalConfirm: () -> Unit,
    onFinalCancel: () -> Unit
) {
    when (state.withdrawalDialogState) {
        WithdrawalDialogState.Warning -> WithdrawalWarningDialog(
            onConfirm = onWarningConfirm,
            onCancel = onWarningCancel
        )

        WithdrawalDialogState.Confirmation -> WithdrawalConfirmationDialog(
            userInputText = state.userInputText,
            countdownSeconds = state.countdownSeconds,
            onUserInputChanged = onUserInputChanged,
            onConfirm = onFinalConfirm,
            onCancel = onFinalCancel
        )

        WithdrawalDialogState.Processing -> WithdrawalProcessingDialog()
        WithdrawalDialogState.Hidden -> Unit
    }
}

@Composable
fun WithdrawalWarningDialog(
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.withdrawal_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(R.string.withdrawal_warning_description),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    BulletPoint(stringResource(R.string.withdrawal_retained_glims))
                    BulletPoint(stringResource(R.string.withdrawal_retained_likes))
                    BulletPoint(stringResource(R.string.withdrawal_retained_profile))
                }
                Text(
                    text = stringResource(R.string.withdrawal_warning_caution),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White
                )
            ) {
                Text(stringResource(R.string.withdrawal_continue))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun WithdrawalConfirmationDialog(
    userInputText: String,
    countdownSeconds: Int,
    onUserInputChanged: (String) -> Unit,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val confirmationText = stringResource(R.string.withdrawal_confirmation_text)
    val isInputValid = userInputText == confirmationText
    val canConfirm = isInputValid && countdownSeconds == 0

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = stringResource(R.string.withdrawal_confirmation_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = stringResource(R.string.withdrawal_confirmation_question),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = stringResource(R.string.withdrawal_rejoin_info),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Black
                )
                Text(
                    text = stringResource(R.string.withdrawal_input_instruction),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = userInputText,
                    onValueChange = onUserInputChanged,
                    placeholder = { Text(confirmationText) },
                    isError = userInputText.isNotEmpty() && !isInputValid,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                if (countdownSeconds > 0) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = stringResource(R.string.withdrawal_countdown, countdownSeconds),
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Black
                        )
                    }
                }
                if (canConfirm) {
                    Text(
                        text = stringResource(R.string.withdrawal_ready),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = canConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black,
                    contentColor = Color.White,
                    disabledContainerColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(stringResource(R.string.withdrawal_confirm))
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onCancel,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
        containerColor = Color.White
    )
}

@Composable
fun WithdrawalProcessingDialog() {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(
                text = stringResource(R.string.withdrawal_processing_title),
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                Text(
                    text = stringResource(R.string.withdrawal_processing_message),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        confirmButton = {},
        containerColor = Color.White
    )
}

@Composable
fun BulletPoint(text: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Top
    ) {
        Text("•", style = MaterialTheme.typography.bodyMedium)
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

// ===== Preview Functions =====
@Preview(showBackground = true)
@Composable
private fun PreviewEditProfileDialog() {
    MaterialTheme {
        EditProfileDialogContainer(
            state = ProfileUiState(editProfileDialogState = EditProfileDialogState.Showing),
            onPersonalInfoClick = {},
            onPasswordChangeClick = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLogoutConfirmationDialog() {
    MaterialTheme {
        LogoutConfirmationDialog(
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLogoutProcessingDialog() {
    MaterialTheme {
        LogoutProcessingDialog()
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalWarningDialog() {
    MaterialTheme {
        WithdrawalWarningDialog(
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalConfirmationDialog() {
    MaterialTheme {
        WithdrawalConfirmationDialog(
            userInputText = "",
            countdownSeconds = 10,
            onUserInputChanged = {},
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalConfirmationDialogReady() {
    MaterialTheme {
        WithdrawalConfirmationDialog(
            userInputText = "탈퇴하겠습니다",
            countdownSeconds = 0,
            onUserInputChanged = {},
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalProcessingDialog() {
    MaterialTheme {
        WithdrawalProcessingDialog()
    }
}
