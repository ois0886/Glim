package com.ssafy.glim.feature.update.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.update.UpdateInfoUiState

@Composable
fun PersonalInfoContent(
    state: UpdateInfoUiState,
    onNameChanged: (TextFieldValue) -> Unit,
    onProfileImageClicked: () -> Unit
) {
    ProfileImageSection(
        imageUri = state.profileImageUri,
        onImageClicked = onProfileImageClicked,
    )

    Spacer(modifier = Modifier.height(24.dp))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = stringResource(id = R.string.profile_label_name))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = state.newName,
            onValueChange = onNameChanged,
            label = { Text(stringResource(id = R.string.profile_hint_name)) },
            isError = state.newNameError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.newNameError != null) {
            Text(
                text = stringResource(state.newNameError),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        } else {
            Text(
                text = stringResource(id = R.string.profile_note_name),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // 이메일 섹션 (읽기 전용)
    EmailSection(email = state.email)
}
