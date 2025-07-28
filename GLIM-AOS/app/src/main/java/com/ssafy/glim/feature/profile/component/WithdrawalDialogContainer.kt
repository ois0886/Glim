// WithdrawalDialogs.kt
package com.ssafy.glim.feature.profile.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.profile.ProfileUiState
import com.ssafy.glim.feature.profile.WithdrawalDialogState

@Composable
fun WithdrawalDialogContainer(
    state: ProfileUiState,
    onWarningConfirm: () -> Unit,
    onWarningCancel: () -> Unit,
    onUserInputChanged: (String) -> Unit,
    onFinalConfirm: () -> Unit,
    onFinalCancel: () -> Unit,
) {
    when (state.withdrawalDialogState) {
        WithdrawalDialogState.Warning -> {
            WithdrawalWarningDialog(
                onConfirm = onWarningConfirm,
                onCancel = onWarningCancel
            )
        }

        WithdrawalDialogState.Confirmation -> {
            WithdrawalConfirmationDialog(
                userInputText = state.userInputText,
                countdownSeconds = state.countdownSeconds,
                onUserInputChanged = onUserInputChanged,
                onConfirm = onFinalConfirm,
                onCancel = onFinalCancel
            )
        }

        WithdrawalDialogState.Processing -> {
            WithdrawalProcessingDialog()
        }

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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(R.string.withdrawal_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = stringResource(R.string.withdrawal_warning_description),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
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
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.withdrawal_continue))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.withdrawal_confirmation_question),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.withdrawal_rejoin_info),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
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
                            color = MaterialTheme.colorScheme.primary
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
                    containerColor = MaterialTheme.colorScheme.error,
                    disabledContainerColor = MaterialTheme.colorScheme.outline
                )
            ) {
                Text(stringResource(R.string.withdrawal_confirm))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onCancel) {
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
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
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
private fun PreviewWithdrawalConfirmationDialog_Empty() {
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
private fun PreviewWithdrawalConfirmationDialog_InvalidInput() {
    MaterialTheme {
        WithdrawalConfirmationDialog(
            userInputText = "탈퇴하겠",
            countdownSeconds = 5,
            onUserInputChanged = {},
            onConfirm = {},
            onCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalConfirmationDialog_Ready() {
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

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalDialogContainer_Warning() {
    val mockState = ProfileUiState(
        withdrawalDialogState = WithdrawalDialogState.Warning
    )

    MaterialTheme {
        WithdrawalDialogContainer(
            state = mockState,
            onWarningConfirm = {},
            onWarningCancel = {},
            onUserInputChanged = {},
            onFinalConfirm = {},
            onFinalCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalDialogContainer_Confirmation() {
    val mockState = ProfileUiState(
        withdrawalDialogState = WithdrawalDialogState.Confirmation,
        userInputText = "탈퇴하겠습니다",
        countdownSeconds = 3
    )

    MaterialTheme {
        WithdrawalDialogContainer(
            state = mockState,
            onWarningConfirm = {},
            onWarningCancel = {},
            onUserInputChanged = {},
            onFinalConfirm = {},
            onFinalCancel = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewWithdrawalDialogContainer_Processing() {
    val mockState = ProfileUiState(
        withdrawalDialogState = WithdrawalDialogState.Processing,
        isWithdrawalLoading = true
    )

    MaterialTheme {
        WithdrawalDialogContainer(
            state = mockState,
            onWarningConfirm = {},
            onWarningCancel = {},
            onUserInputChanged = {},
            onFinalConfirm = {},
            onFinalCancel = {}
        )
    }
}
