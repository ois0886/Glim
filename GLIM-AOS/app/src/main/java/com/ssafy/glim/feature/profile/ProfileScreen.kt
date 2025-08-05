package com.ssafy.glim.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
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
        onRefresh = viewModel::loadProfileData,
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
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pullToRefreshState = rememberPullToRefreshState()

    PullToRefreshBox(
        isRefreshing = state.isLoading,
        onRefresh = onRefresh,
        state = pullToRefreshState,
        modifier = modifier
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(top = 24.dp),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                ProfileHeaderSection(state = state)
            }

            item {
                MyGlimsSectionWithLoading(
                    state = state,
                    navigateToGlimUploadList = navigateToGlimUploadList,
                    navigateToGlimLikedList = navigateToGlimLikedList
                )
            }

            item {
                Spacer(modifier = Modifier.height(12.dp))
                GlimGrassWithLoading(state = state)
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

@Composable
private fun ProfileHeaderSection(
    state: ProfileUiState
) {
    Column {
        if (state.isProfileLoading) {
            ProfileHeader(profileImageUrl = null, userName = "")
        } else {
            ProfileHeader(
                profileImageUrl = state.profileImageUrl,
                userName = state.userName
            )
        }

        if (state.profileError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.error_load_profile_failed),
                style = MaterialTheme.typography.bodySmall,
                color = Color.Red,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MyGlimsSectionWithLoading(
    state: ProfileUiState,
    navigateToGlimUploadList: () -> Unit,
    navigateToGlimLikedList: () -> Unit
) {
    when {
        state.isQuotesLoading || state.isLikedQuotesLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.quotesError || state.likedQuotesError -> {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.error_load_quotes_failed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            MyGlimsSection(
                navigateToGlimUploadList,
                navigateToGlimLikedList,
                state.publishedGlimCount,
                state.likedGlimCount
            )
        }
    }
}

@Composable
private fun GlimGrassWithLoading(
    state: ProfileUiState
) {
    when {
        state.isQuotesLoading -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.quotesError -> {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(R.string.error_load_quotes_for_grass),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.pull_to_refresh_hint),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        else -> {
            GlimGrassGrid(
                uploadQuotes = state.uploadQuotes,
                firstUploadDateStr = state.firstUploadDate
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreenLoading() {
    val mockState = ProfileUiState(
        isProfileLoading = true,
        isQuotesLoading = true
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
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreenError() {
    val mockState = ProfileUiState(
        userName = "박성준",
        profileError = true,
        quotesError = true
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
            onRefresh = {}
        )
    }
}
