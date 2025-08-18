package com.ssafy.glim.feature.post.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ssafy.glim.R

@Composable
fun ExitConfirmDialog(
    onCancelExit: () -> Unit,
    onConfirmExit: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelExit,
        title = {
            Text(
                text = stringResource(R.string.exit_dialog_title),
                color = Color.Black,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = { Text(stringResource(R.string.exit_dialog_description), color = Color.Black) },
        confirmButton = {
            TextButton(
                onClick = onConfirmExit,
            ) {
                Text(stringResource(R.string.exit), color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelExit,
            ) {
                Text(stringResource(R.string.cancel), color = Color.Black)
            }
        },
        containerColor = Color.White
    )
}
