package com.ssafy.glim.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.feature.profile.component.EditProfileDialogContainer
import com.ssafy.glim.feature.profile.component.GlimGrassGrid
import com.ssafy.glim.feature.profile.component.LogoutDialogContainer
import com.ssafy.glim.feature.profile.component.MyGlimsSection
import com.ssafy.glim.feature.profile.component.ProfileHeader
import com.ssafy.glim.feature.profile.component.SettingsSection
import com.ssafy.glim.feature.profile.component.WithdrawalButton
import com.ssafy.glim.feature.profile.component.WithdrawalDialogContainer
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ProfileRoute(
    padding: PaddingValues,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProfileData()
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.ShowError -> Toast.makeText(
                context,
                context.getString(sideEffect.messageRes),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    ProfileScreen(
        state = state,
        onLogOutClick = viewModel::onLogOutClick,
        onLogoutConfirm = viewModel::onLogoutConfirm,
        onLogoutCancel = viewModel::onLogoutCancel,
        navigateToEditProfile = viewModel::navigateToEditProfile,
        navigateToGlimUploadList = viewModel::navigateToGlimsUpload,
        navigateToGlimLikedList = viewModel::navigateToGlimsLiked,
        navigateToSettings = viewModel::navigateToSettings,
        onWithdrawalClick = viewModel::onWithdrawalClick,
        onWarningConfirm = viewModel::onWarningConfirm,
        onWarningCancel = viewModel::onWarningCancel,
        onUserInputChanged = viewModel::onUserInputChanged,
        onFinalConfirm = viewModel::onFinalConfirm,
        onFinalCancel = viewModel::onFinalCancel,
        onPersonalInfoClick = viewModel::navigateToPersonalInfo,
        onPasswordChangeClick = viewModel::navigateToPasswordChange,
        onEditProfileDialogCancel = viewModel::onEditProfileDialogCancel,
        loadProfileData = viewModel::loadProfileData,
        modifier = Modifier.padding(padding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onLogOutClick: () -> Unit,
    onLogoutConfirm: () -> Unit,
    onLogoutCancel: () -> Unit,
    navigateToEditProfile: () -> Unit,
    navigateToGlimUploadList: () -> Unit,
    navigateToGlimLikedList: () -> Unit,
    navigateToSettings: () -> Unit,
    onWithdrawalClick: () -> Unit,
    onWarningConfirm: () -> Unit,
    onWarningCancel: () -> Unit,
    onUserInputChanged: (String) -> Unit,
    onFinalConfirm: () -> Unit,
    onFinalCancel: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    onPasswordChangeClick: () -> Unit,
    onEditProfileDialogCancel: () -> Unit,
    loadProfileData: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = loadProfileData,
        state = pullToRefreshState,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProfileHeader(
                    profileImageUrl = state.profileImageUrl,
                    userName = state.userName,
                    error = state.error
                )
            }

            item {
                MyGlimsSection(
                    navigateToGlimUploadList = navigateToGlimUploadList,
                    navigateToGlimLikedList = navigateToGlimLikedList,
                    publishedGlimCount = state.publishedGlimCount,
                    likedGlimCount = state.likedGlimCount,
                    error = state.error
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Box(modifier = Modifier.padding(8.dp)) {
                        GlimGrassGrid(
                            uploadQuotes = state.uploadQuotes,
                            error = state.error
                        )
                    }
                }
            }

            item {
                SettingsSection(
                    navigateToEditProfile = navigateToEditProfile,
                    navigateToSettings = navigateToSettings,
                    onLogOutClick = onLogOutClick
                )
            }

            item {
                WithdrawalButton(onWithdrawalClick)
            }
        }
    }

    LogoutDialogContainer(
        state = state,
        onLogoutConfirm = onLogoutConfirm,
        onLogoutCancel = onLogoutCancel
    )

    WithdrawalDialogContainer(
        state = state,
        onWarningConfirm = onWarningConfirm,
        onWarningCancel = onWarningCancel,
        onUserInputChanged = onUserInputChanged,
        onFinalConfirm = onFinalConfirm,
        onFinalCancel = onFinalCancel
    )

    EditProfileDialogContainer(
        state = state,
        onPersonalInfoClick = onPersonalInfoClick,
        onPasswordChangeClick = onPasswordChangeClick,
        onCancel = onEditProfileDialogCancel
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    val mockState = ProfileUiState(
        userName = "박성준",
        publishedGlimCount = 15,
        likedGlimCount = 8,
        error = false
    )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            navigateToEditProfile = {},
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            navigateToSettings = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onWarningConfirm = {},
            onWarningCancel = {},
            onUserInputChanged = {},
            onFinalConfirm = {},
            onFinalCancel = {},
            onLogoutCancel = {},
            onLogoutConfirm = {},
            onPersonalInfoClick = {},
            onPasswordChangeClick = {},
            onEditProfileDialogCancel = {},
            loadProfileData = {}
        )
    }
}

@Preview(showBackground = true, name = "With Error")
@Composable
private fun PreviewProfileScreenWithError() {
    val mockState = ProfileUiState(
        userName = "박성준",
        publishedGlimCount = 15,
        likedGlimCount = 8,
        error = true
    )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            navigateToEditProfile = {},
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            navigateToSettings = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onWarningConfirm = {},
            onWarningCancel = {},
            onUserInputChanged = {},
            onFinalConfirm = {},
            onFinalCancel = {},
            onLogoutCancel = {},
            onLogoutConfirm = {},
            onPersonalInfoClick = {},
            onPasswordChangeClick = {},
            onEditProfileDialogCancel = {},
            loadProfileData = {}
        )
    }
}
