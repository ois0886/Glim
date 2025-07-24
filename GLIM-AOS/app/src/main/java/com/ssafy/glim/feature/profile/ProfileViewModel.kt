package com.ssafy.glim.feature.profile

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.usecase.user.DeleteUserUseCase
import com.ssafy.glim.core.domain.usecase.user.GetUserByEmailUseCase
import com.ssafy.glim.core.domain.usecase.user.UpdateUserUseCase
import com.ssafy.glim.core.navigation.GlimRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val navigator: Navigator,
    private val getUserByEmailUseCase: GetUserByEmailUseCase,
    private val updateUserUseCase: UpdateUserUseCase,
    private val deleteUserUseCase: DeleteUserUseCase,
) : ViewModel(), ContainerHost<ProfileUiState, ProfileSideEffect> {

    override val container: Container<ProfileUiState, ProfileSideEffect> =
        container(initialState = ProfileUiState())

    init {
        loadProfileData()
    }

    fun navigateToGlimLikedList() = intent {
        navigator.navigate(GlimRoute.Liked)
    }

    fun navigateToGlimUploadList() = intent {
        navigator.navigate(GlimRoute.Upload)
    }

    fun navigateToEditProfile() = intent {
        navigator.navigate(Route.UpdateInfo)
    }

    fun navigateToLockSettings() = intent {
        // TODO: 잠금 설정 화면으로 이동
        postSideEffect(ProfileSideEffect.ShowToast(R.string.lock_settings_message))
    }

    fun navigateToNotificationSettings() = intent {
        // TODO: 알림 설정 화면으로 이동
        postSideEffect(ProfileSideEffect.ShowToast(R.string.notification_settings_message))
    }

    private fun loadProfileData() = intent {
        reduce { state.copy(isLoading = true) }

        runCatching {
            // TODO: 실제 사용자 이메일 가져오기 (토큰에서 추출 등)
            val userEmail = "user@example.com"
            getUserByEmailUseCase(userEmail)
        }.onSuccess { user ->
            reduce {
                state.copy(
                    isLoading = false,
                    userName = user.nickname,
                    // user = user,
                    publishedGlimCount = 24,
                    likedGlimCount = 8,
                    glimShortCards = createMockGlimShortCards(),
                    profileImageUrl = "https://example.com/profile.jpg",
                )
            }
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
        }
    }

    fun updateProfile(
        password: String,
        nickname: String,
        gender: String,
        birthDate: String
    ) = intent {
        // val user = state.user ?: return@intent

        reduce { state.copy(isLoading = true) }

        runCatching {
            updateUserUseCase(
                memberId = 3,
                password = password,
                nickname = nickname,
                gender = gender,
                birthDate = birthDate
            )
        }.onSuccess { updatedUser ->
            reduce {
                state.copy(
                    isLoading = false,
                    // user = updatedUser,
                    userName = updatedUser.nickname
                )
            }
            postSideEffect(ProfileSideEffect.ShowToast(R.string.profile_update_success))
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(ProfileSideEffect.ShowError(R.string.profile_update_failed))
        }
    }

    // ========== 로그아웃 ==========
    fun onLogOutClick() = intent {
        // TODO: 로그아웃 처리 (토큰 삭제 등)
        postSideEffect(ProfileSideEffect.ShowToast(R.string.logout_success))
        navigator.navigate(Route.Login)
    }

    // ========== 회원탈퇴 ==========
    fun onWithdrawalClick() = intent {
        // val user = state.user ?: return@intent

        reduce { state.copy(isLoading = true) }

        runCatching {
            deleteUserUseCase(3)
        }.onSuccess { deletedUser ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(ProfileSideEffect.ShowToast(R.string.withdrawal_success))
            // TODO: 토큰 삭제 등
            navigator.navigate(Route.Login)
        }.onFailure { exception ->
            reduce { state.copy(isLoading = false) }
            postSideEffect(ProfileSideEffect.ShowToast(R.string.withdrawal_failed))
        }
    }

    // ========== 글림 좋아요 토글 ==========
    fun onGlimLikeToggle(glimId: String) = intent {
        val currentGlims = state.glimShortCards
        val updatedGlims = currentGlims.map { glim ->
            if (glim.id == glimId) {
                glim.copy(
                    isLiked = !glim.isLiked,
                    likeCount = if (glim.isLiked) glim.likeCount - 1 else glim.likeCount + 1,
                )
            } else {
                glim
            }
        }

        reduce { state.copy(glimShortCards = updatedGlims) }

        // TODO: 서버에 좋아요 상태 업데이트 API 호출
        val toggledGlim = updatedGlims.find { it.id == glimId }
        val messageRes = if (toggledGlim?.isLiked == true) {
            R.string.like_added_message
        } else {
            R.string.like_removed_message
        }
        postSideEffect(ProfileSideEffect.ShowToast(messageRes))
    }

    private fun createMockGlimShortCards() = listOf(
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
    )
}
