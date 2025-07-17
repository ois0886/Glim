package com.example.myapplication.feature.profile

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
import com.example.myapplication.feature.profile.component.ProfileHeader
import com.example.myapplication.feature.profile.component.RecentArticlesSection
import com.example.myapplication.feature.profile.component.SettingsSection
import com.example.myapplication.feature.profile.component.StatisticsSection
import com.example.myapplication.feature.profile.component.WithdrawalButton
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ProfileRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ProfileSideEffect.ShowToast -> {
                // TODO: Toast 표시
            }
            is ProfileSideEffect.ShowError -> {
                // TODO: 에러 표시
            }
        }
    }

    ProfileScreen(
        state = state,
        onEditProfileClick = viewModel::onEditProfileClicked,
        onViewAllRecentArticlesClick = viewModel::onViewAllRecentArticlesClicked,
        onPersonalInfoClick = viewModel::onPersonalInfoClicked,
        onAccountSettingsClick = viewModel::onAccountSettingsClicked,
        onNotificationSettingsClick = viewModel::onNotificationSettingsClicked,
        onLogOutClick = viewModel::onLogOutClick,
        onWithdrawalClick = viewModel::onWithdrawalClick,
        onArticleLikeClick = viewModel::onArticleLikeClicked,
        modifier = Modifier.padding(padding)
    )
}

@Composable
private fun ProfileScreen(
    state: ProfileUiState,
    onEditProfileClick: () -> Unit,
    onViewAllRecentArticlesClick: () -> Unit,
    onPersonalInfoClick: () -> Unit,
    onAccountSettingsClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLogOutClick: () -> Unit,
    onWithdrawalClick: () -> Unit,
    onArticleLikeClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            ProfileHeader(
                profileImageUrl = state.profileImageUrl,
                userName = state.userName,
                onEditClick = onEditProfileClick
            )
        }

        item {
            StatisticsSection(
                publishedCount = state.publishedArticleCount,
                likedCount = state.likedArticleCount
            )
        }

        item {
            RecentArticlesSection(
                articles = state.recentArticles,
                onViewAllClick = onViewAllRecentArticlesClick,
                onArticleLikeClick = onArticleLikeClick
            )
        }

        item {
            SettingsSection(
                onPersonalInfoClick = onPersonalInfoClick,
                onAccountSettingsClick = onAccountSettingsClick,
                onNotificationSettingsClick = onNotificationSettingsClick,
                onLogOutClick = onLogOutClick
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
    val mockState = ProfileUiState(
        profileImageUrl = null,
        userName = "박성준",
        publishedArticleCount = 24,
        likedArticleCount = 8,
        isLoading = false,
        recentArticles = listOf(
            RecentArticle(
                id = "1",
                title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                timestamp = "P.51",
                likeCount = 1247,
                isLiked = false
            ),
            RecentArticle(
                id = "2",
                title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                timestamp = "P.51",
                likeCount = 856,
                isLiked = true
            )
        )
    )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            onEditProfileClick = {},
            onViewAllRecentArticlesClick = {},
            onPersonalInfoClick = {},
            onAccountSettingsClick = {},
            onNotificationSettingsClick = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onArticleLikeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewEmptyArticles() {
    val mockState = ProfileUiState(
        profileImageUrl = null,
        userName = "박성준",
        publishedArticleCount = 0,
        likedArticleCount = 0,
        isLoading = false,
        recentArticles = emptyList()
    )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            onEditProfileClick = {},
            onViewAllRecentArticlesClick = {},
            onPersonalInfoClick = {},
            onAccountSettingsClick = {},
            onNotificationSettingsClick = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onArticleLikeClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewLoadingState() {
    val mockState = ProfileUiState(
        profileImageUrl = null,
        userName = "",
        publishedArticleCount = 0,
        likedArticleCount = 0,
        isLoading = true,
        recentArticles = emptyList()
    )

    MaterialTheme {
        ProfileScreen(
            state = mockState,
            onEditProfileClick = {},
            onViewAllRecentArticlesClick = {},
            onPersonalInfoClick = {},
            onAccountSettingsClick = {},
            onNotificationSettingsClick = {},
            onLogOutClick = {},
            onWithdrawalClick = {},
            onArticleLikeClick = {}
        )
    }
}
