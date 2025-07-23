package com.ssafy.glim.feature.updateInfo

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.feature.updateInfo.component.EmailSection
import com.ssafy.glim.feature.updateInfo.component.ProfileImageSection
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun UpdateProfileRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: UpdateInfoViewModel = hiltViewModel(),
) {
    val uiState by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    val imagePickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
        ) { uri: Uri? ->
            uri?.let {
                viewModel.onImageSelected(it)
            }
        }
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is UpdateInfoSideEffect.ShowErrorRes ->
                Toast.makeText(context, context.getString(effect.messageRes), Toast.LENGTH_SHORT)
                    .show()

            is UpdateInfoSideEffect.ShowImagePicker -> {
                imagePickerLauncher.launch("image/*")
            }

            is UpdateInfoSideEffect.ProfileUpdated -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.success_update_profile),
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    UpdateProfileScreen(
        state = uiState,
        padding = padding,
        onNameChanged = viewModel::onNameChanged,
        onProfileImageClicked = viewModel::onProfileImageClicked,
        onSaveClicked = viewModel::onSaveClicked,
        onBackClick = viewModel::onBackClicked,
    )
}

@Composable
internal fun UpdateProfileScreen(
    state: UpdateInfoUiState,
    padding: PaddingValues,
    onNameChanged: (String) -> Unit,
    onProfileImageClicked: () -> Unit,
    onSaveClicked: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(padding.excludeSystemBars()),
    ) {
        GlimTopBar(
            title = stringResource(id = R.string.edit_profile_title),
            showBack = true,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp,
            onBack = onBackClick,
        )

        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ProfileImageSection(
                imageUri = state.profileImageUri,
                onImageClicked = onProfileImageClicked,
            )

            Column(modifier = Modifier.fillMaxWidth()) {
                Text(text = stringResource(id = R.string.profile_label_name))
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = state.name,
                    onValueChange = onNameChanged,
                    label = { Text(stringResource(id = R.string.profile_hint_name)) },
                    isError = state.nameError != null,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (state.nameError != null) {
                    Text(
                        text = stringResource(state.nameError),
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

            // 이메일 섹션
            EmailSection(email = state.email)

            Spacer(modifier = Modifier.height(32.dp))

            GlimButton(
                text =
                    if (state.isLoading) {
                        stringResource(R.string.updating)
                    } else {
                        stringResource(R.string.update)
                    },
                onClick = onSaveClicked,
                enabled = state.isSaveEnabled && !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateProfileScreenPreview() {
    UpdateProfileScreen(
        state =
            UpdateInfoUiState(
                profileImageUri = null,
                name = "홍길동",
                nameError = null,
                email = "hong@example.com",
                isLoading = false,
            ),
        padding = PaddingValues(0.dp),
        onNameChanged = {},
        onProfileImageClicked = {},
        onSaveClicked = {},
        onBackClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun UpdateProfileScreenErrorPreview() {
    UpdateProfileScreen(
        state =
            UpdateInfoUiState(
                profileImageUri = null,
                name = "",
                nameError = R.string.error_name_empty,
                email = "hong@example.com",
                isLoading = false,
            ),
        padding = PaddingValues(0.dp),
        onNameChanged = {},
        onProfileImageClicked = {},
        onSaveClicked = {},
        onBackClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun ProfileImageSectionPreview() {
    ProfileImageSection(
        imageUri = null,
        onImageClicked = {},
    )
}

@Preview(showBackground = true)
@Composable
fun EmailSectionPreview() {
    EmailSection(email = "hong@example.com")
}
