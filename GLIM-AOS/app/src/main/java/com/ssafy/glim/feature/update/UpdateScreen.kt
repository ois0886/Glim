package com.ssafy.glim.feature.update

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.ssafy.glim.feature.update.component.PasswordChangeContent
import com.ssafy.glim.feature.update.component.PersonalInfoContent
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
    onNameChanged: (String) -> Unit,
    onProfileImageClicked: () -> Unit,
    onCurrentPasswordChanged: (String) -> Unit,
    onNewPasswordChanged: (String) -> Unit,
    onConfirmPasswordChanged: (String) -> Unit,
    onSaveClicked: () -> Unit,
    onBackClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding.excludeSystemBars()),
    ) {
        GlimTopBar(
            title = when (state.updateType) {
                UpdateType.PERSONAL -> stringResource(id = R.string.edit_profile_title)
                UpdateType.PASSWORD -> stringResource(id = R.string.change_password_title)
            },
            showBack = true,
            alignment = TitleAlignment.Center,
            titleColor = Color.Black,
            titleSize = 20.sp,
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

            Spacer(modifier = Modifier.height(32.dp))

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
                enabled = state.isSaveEnabled && !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UpdateProfileScreenPreview() {
    UpdateScreen(
        state =
        UpdateInfoUiState(
            email = "hong@example.com",
            newName = "홍길동",
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

@Preview(showBackground = true)
@Composable
fun UpdateProfileScreenErrorPreview() {
    UpdateScreen(
        state =
        UpdateInfoUiState(
            email = "hong@example.com",
            newNameError = R.string.error_name_empty,
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
