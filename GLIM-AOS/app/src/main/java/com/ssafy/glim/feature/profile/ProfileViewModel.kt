package com.ssafy.glim.feature.profile

import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.navigation.GlimRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val navigator: Navigator,
) : ViewModel(), ContainerHost<ProfileUiState, ProfileSideEffect> {
    override val container: Container<ProfileUiState, ProfileSideEffect> =
        container(initialState = ProfileUiState())

    init {
        loadProfileData()
    }

    // 네비게이션 함수들
    fun navigateToGlimLikedList() =
        intent {
            navigator.navigate(GlimRoute.Liked)
        }

    fun navigateToGlimUploadList() =
        intent {
            navigator.navigate(GlimRoute.Upload)
        }

    fun navigateToEditProfile() =
        intent {
            navigator.navigate(Route.UpdateInfo)
        }

    fun navigateToLockSettings() =
        intent {
            // TODO: 잠금 설정 화면으로 이동
            postSideEffect(ProfileSideEffect.ShowToast(R.string.lock_settings_message))
        }

    fun navigateToNotificationSettings() =
        intent {
            // TODO: 알림 설정 화면으로 이동
            postSideEffect(ProfileSideEffect.ShowToast(R.string.notification_settings_message))
        }

    // 액션 함수들
    fun onLogOutClick() =
        intent {
            // TODO: 로그아웃 처리
            postSideEffect(ProfileSideEffect.ShowToast(R.string.logout_success))
            navigator.navigate(Route.Login)
        }

    fun onWithdrawalClick() =
        intent {
            // TODO: 회원탈퇴 처리
            postSideEffect(ProfileSideEffect.ShowToast(R.string.withdrawal_message))
            navigator.navigate(Route.Login)
        }

    fun onGlimLikeToggle(glimId: String) =
        intent {
            val currentGlims = state.glimShortCards
            val updatedGlims =
                currentGlims.map { glim ->
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

            // TODO: 서버에 좋아요 상태 업데이트
            val toggledGlim = updatedGlims.find { it.id == glimId }
            val messageRes =
                if (toggledGlim?.isLiked == true) {
                    R.string.like_added_message
                } else {
                    R.string.like_removed_message
                }
            postSideEffect(ProfileSideEffect.ShowToast(messageRes))
        }

    private fun loadProfileData() =
        intent {
            reduce { state.copy(isLoading = true) }

            try {
                // TODO: 실제 API 호출로 교체
                val mockGlimShortCards =
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
                    )

                reduce {
                    state.copy(
                        isLoading = false,
                        userName = "박성준",
                        publishedGlimCount = 24,
                        likedGlimCount = 8,
                        glimShortCards = mockGlimShortCards,
                        profileImageUrl = "https://example.com/profile.jpg",
                    )
                }
            } catch (e: Exception) {
                reduce { state.copy(isLoading = false) }
                postSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
            }
        }
}
