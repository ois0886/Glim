package com.ssafy.glim.feature.profile

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.feature.profile.component.MyGlimsSection
import com.ssafy.glim.feature.profile.component.ProfileHeader
import com.ssafy.glim.feature.profile.component.SettingsSection
import com.ssafy.glim.feature.profile.component.UploadGlimCardListSection
import com.ssafy.glim.feature.profile.component.WithdrawalButton
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ProfileRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.ShowToast -> {
                Toast.makeText(
                    context,
                    context.getString(sideEffect.messageRes),
                    Toast.LENGTH_SHORT,
                ).show()
            }

            is ProfileSideEffect.ShowError -> {
                Toast.makeText(
                    context,
                    context.getString(sideEffect.messageRes),
                    Toast.LENGTH_LONG,
                ).show()
            }
        }
    }

    ProfileScreen(
        state = state,
        navigateToEditProfile = viewModel::navigateToEditProfile,
        navigateToGlimUploadList = viewModel::navigateToGlimUploadList,
        navigateToGlimLikedList = viewModel::navigateToGlimLikedList,
        navigateToLockSettings = viewModel::navigateToLockSettings,
        navigateToNotificationSettings = viewModel::navigateToNotificationSettings,
        onLogOutClick = viewModel::onLogOutClick,
        onWithdrawalClick = viewModel::onWithdrawalClick,
        onGlimLikeToggle = viewModel::onGlimLikeToggle,
        modifier = Modifier.padding(padding),
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    navigateToEditProfile: () -> Unit,
    navigateToGlimUploadList: () -> Unit,
    navigateToGlimLikedList: () -> Unit,
    navigateToLockSettings: () -> Unit,
    navigateToNotificationSettings: () -> Unit,
    onLogOutClick: () -> Unit,
    onWithdrawalClick: () -> Unit,
    onGlimLikeToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
        modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        item {
            ProfileHeader(
                profileImageUrl = state.profileImageUrl,
                userName = state.userName,
            )
        }

        item {
            MyGlimsSection(
                navigateToGlimUploadList = navigateToGlimUploadList,
                navigateToGlimLikedList = navigateToGlimLikedList,
                publishedGlimCount = state.publishedGlimCount,
                likedGlimCount = state.likedGlimCount,
            )
        }

        item {
            UploadGlimCardListSection(
                glimCards = state.glimShortCards,
                navigateToGlimUploadList = navigateToGlimUploadList,
                onGlimLikeToggle = onGlimLikeToggle,
            )
        }

        item {
            SettingsSection(
                navigateToEditProfile = navigateToEditProfile,
                navigateToAccountSettings = navigateToLockSettings,
                navigateToNotificationSettings = navigateToNotificationSettings,
                onLogOutClick = onLogOutClick,
            )
        }

        item {
            WithdrawalButton(onWithdrawalClick = onWithdrawalClick)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreen() {
    val mockState =
        ProfileUiState(
            profileImageUrl = null,
            userName = "박성준",
            publishedGlimCount = 24,
            likedGlimCount = 8,
            isLoading = false,
            glimShortCards =
            listOf(
                GlimShortCard(
                    id = "1",
                    title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                    timestamp = "P.51",
                    likeCount = 1247,
                    isLiked = false,
                ),
                GlimShortCard(
                    id = "2",
                    title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                    timestamp = "P.51",
                    likeCount = 856,
                    isLiked = true,
                ),
            ),
        )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            navigateToEditProfile = {},
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            navigateToLockSettings = {},
            navigateToNotificationSettings = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onGlimLikeToggle = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmptyGlimCards() {
    val mockState =
        ProfileUiState(
            profileImageUrl = null,
            userName = "박성준",
            publishedGlimCount = 0,
            likedGlimCount = 0,
            isLoading = false,
            glimShortCards = emptyList(),
        )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            navigateToEditProfile = {},
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            navigateToLockSettings = {},
            navigateToNotificationSettings = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onGlimLikeToggle = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingState() {
    val mockState =
        ProfileUiState(
            profileImageUrl = null,
            userName = "",
            publishedGlimCount = 0,
            likedGlimCount = 0,
            isLoading = true,
            glimShortCards = emptyList(),
        )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            navigateToEditProfile = {},
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            navigateToLockSettings = {},
            navigateToNotificationSettings = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onGlimLikeToggle = {},
        )
    }
}
