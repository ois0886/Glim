package com.ssafy.glim.feature.post.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ssafy.glim.R

@Composable
fun ExitConfirmDialog(
    onCancelExit: () -> Unit,
    onConfirmExit: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onCancelExit,
        title = { Text("나가시겠습니까?") },
        text = { Text("작성 중인 내용이 사라집니다.") },
        confirmButton = {
            TextButton(
                onClick = onConfirmExit,
            ) {
                Text(stringResource(R.string.exit))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancelExit,
            ) {
                Text(stringResource(R.string.cancel))
            }
        },
    )
}
