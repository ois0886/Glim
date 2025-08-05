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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import com.ssafy.glim.core.domain.model.UploadQuote
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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
        onRefreshProfile = viewModel::refreshProfile,
        onRefreshQuotes = viewModel::refreshUploadQuotes,
        modifier = Modifier.padding(padding)
    )
}

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
    onRefreshProfile: () -> Unit,
    onRefreshQuotes: () -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 24.dp),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 프로필 헤더 - 프로필 로딩 상태에 따라 처리
        item {
            ProfileHeaderWithLoading(
                state = state,
                onRefresh = onRefreshProfile
            )
        }

        // 내 글림 섹션 - 글림 로딩 상태에 따라 처리
        item {
            MyGlimsSectionWithLoading(
                state = state,
                navigateToGlimUploadList = navigateToGlimUploadList,
                navigateToGlimLikedList = navigateToGlimLikedList,
                onRefresh = onRefreshQuotes
            )
        }

        // 잔디밭 - 글림 데이터 로딩 상태에 따라 처리
        item {
            Spacer(modifier = Modifier.height(12.dp))
            GlimGrassWithLoading(
                state = state
            )
        }

        // 설정 섹션
        item {
            SettingsSection(
                navigateToEditProfile = navigateToEditProfile,
                navigateToSettings = navigateToSettings,
                onLogOutClick = onLogOutClick
            )
        }

        item { WithdrawalButton(onWithdrawalClick) }
    }

    // 다이얼로그들
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
private fun ProfileHeaderWithLoading(
    state: ProfileUiState,
    onRefresh: () -> Unit
) {
    when {
        state.isProfileLoading -> {
            // 프로필 로딩 중
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.profileError -> {
            // 프로필 로드 에러
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.error_load_profile_failed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onRefresh) {
                    Text(stringResource(R.string.retry))
                }
            }
        }

        else -> {
            // 프로필 정상 표시
            ProfileHeader(state.profileImageUrl, state.userName)
        }
    }
}

@Composable
private fun MyGlimsSectionWithLoading(
    state: ProfileUiState,
    navigateToGlimUploadList: () -> Unit,
    navigateToGlimLikedList: () -> Unit,
    onRefresh: () -> Unit
) {
    when {
        state.isQuotesLoading -> {
            // 글림 데이터 로딩 중
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.quotesError -> {
            // 글림 데이터 로드 에러
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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = onRefresh) {
                    Text(stringResource(R.string.retry))
                }
            }
        }

        else -> {
            // 글림 섹션 정상 표시
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
            // 잔디밭 로딩 중
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
            // 잔디밭 에러 (별도 새로고침 버튼 제공하지 않음 - 위에서 이미 제공)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.error_load_quotes_for_grass),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }

        else -> {
            // 잔디밭 정상 표시
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
            onRefreshProfile = {},
            onRefreshQuotes = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreenError() {
    val mockState = ProfileUiState(
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
            onRefreshProfile = {},
            onRefreshQuotes = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileScreenSuccess() {
    val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val firstUploadDate = today.minusDays(89)
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    val mockUploadQuotes = (0..30).map { i ->
        UploadQuote(
            quoteId = i.toLong(),
            content = "영감을 주는 글귀 $i",
            views = (0..500).random().toLong(),
            page = (1..300).random(),
            likeCount = (0..100).random().toLong(),
            createdAt = "${firstUploadDate.plusDays(i.toLong()).format(fmt)}T10:00:00.000000",
            liked = (0..4).random() == 0
        )
    }

    val mockState = ProfileUiState(
        userName = "박성준",
        publishedGlimCount = mockUploadQuotes.size,
        likedGlimCount = mockUploadQuotes.count { it.liked },
        uploadQuotes = mockUploadQuotes,
        firstUploadDate = firstUploadDate.format(fmt),
        isLoading = false,
        profileError = false,
        quotesError = false
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
            onRefreshProfile = {},
            onRefreshQuotes = {}
        )
    }
}
