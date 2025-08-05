package com.ssafy.glim.feature.update

import PersonalInfoContent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.auth.login.component.GlimButton
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.feature.update.component.PasswordChangeContent
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun UpdateRoute(
    updateType: UpdateType,
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: UpdateViewModel = hiltViewModel(),
) {
    val uiState by viewModel.container.stateFlow.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(updateType) {
        viewModel.setUpdateType(updateType)
    }

    LaunchedEffect(Unit) {
        viewModel.getUseCurrentInfo()
    }

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
            is UpdateInfoSideEffect.ShowError ->
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
                popBackStack()
            }

            is UpdateInfoSideEffect.PasswordUpdated -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.success_update_password),
                    Toast.LENGTH_SHORT,
                ).show()
                popBackStack()
            }
        }
    }

    UpdateScreen(
        state = uiState,
        padding = padding,
        onNameChanged = viewModel::onNameChanged,
        onProfileImageClicked = viewModel::onProfileImageClicked,
        onCurrentPasswordChanged = viewModel::onCurrentPasswordChanged,
        onNewPasswordChanged = viewModel::onNewPasswordChanged,
        onConfirmPasswordChanged = viewModel::onConfirmPasswordChanged,
        onSaveClicked = viewModel::onSaveClicked,
        onBackClick = { popBackStack() },
    )
}

@Composable
internal fun UpdateScreen(
    state: UpdateInfoUiState,
    padding: PaddingValues,
    onNameChanged: (TextFieldValue) -> Unit,
    onProfileImageClicked: () -> Unit,
    onCurrentPasswordChanged: (TextFieldValue) -> Unit,
    onNewPasswordChanged: (TextFieldValue) -> Unit,
    onConfirmPasswordChanged: (TextFieldValue) -> Unit,
    onSaveClicked: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding.excludeSystemBars())
            .imePadding()
            .navigationBarsPadding()
    ) {
        GlimTopBar(
            title = when (state.updateType) {
                UpdateType.PERSONAL -> stringResource(id = R.string.edit_profile_title)
                UpdateType.PASSWORD -> stringResource(id = R.string.change_password_title)
            },
            showBack = true,
            alignment = TitleAlignment.Center,
            onBack = onBackClick,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            when (state.updateType) {
                UpdateType.PERSONAL -> {
                    PersonalInfoContent(
                        state = state,
                        onNameChanged = onNameChanged,
                        onProfileImageClicked = onProfileImageClicked
                    )
                }

                UpdateType.PASSWORD -> {
                    PasswordChangeContent(
                        state = state,
                        onCurrentPasswordChanged = onCurrentPasswordChanged,
                        onNewPasswordChanged = onNewPasswordChanged,
                        onConfirmPasswordChanged = onConfirmPasswordChanged
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = when {
                    state.isLoading && state.updateType == UpdateType.PERSONAL ->
                        stringResource(R.string.updating_profile)

                    state.isLoading && state.updateType == UpdateType.PASSWORD ->
                        stringResource(R.string.updating_password)

                    state.updateType == UpdateType.PERSONAL ->
                        stringResource(R.string.update_profile)

                    else -> stringResource(R.string.update_password)
                },
                onClick = onSaveClicked,
                enabled = state.isSaveEnabled,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateProfileScreenPreview() {
    UpdateScreen(
        state = UpdateInfoUiState(
            email = "hong@example.com",
            name = "홍길동",
            newName = TextFieldValue("홍길동2"),
            gender = "남자",
            birthDate = "1990-01-01"
        ),
        padding = PaddingValues(0.dp),
        onNameChanged = {},
        onProfileImageClicked = {},
        onSaveClicked = {},
        onBackClick = {},
        onNewPasswordChanged = {},
        onConfirmPasswordChanged = {},
        onCurrentPasswordChanged = {}
    )
}
