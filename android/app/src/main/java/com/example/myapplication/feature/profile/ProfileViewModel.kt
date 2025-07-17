package com.example.myapplication.feature.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.myapplication.R
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    @ApplicationContext private val context: Context, private val navigator: Navigator
) : ViewModel(), ContainerHost<ProfileUiState, ProfileSideEffect> {

    override val container: Container<ProfileUiState, ProfileSideEffect> =
        container(initialState = ProfileUiState())

    init {
        loadProfileData()
    }

    fun onEditProfileClicked() = intent {}

    fun onViewAllRecentArticlesClicked() = intent {}

    fun onPersonalInfoClicked() = intent {}

    fun onAccountSettingsClicked() = intent {}

    fun onNotificationSettingsClicked() = intent {}

    fun onLogOutClick() = intent {}

    fun onWithdrawalClick() = intent {
        // 로그아웃 처리
        postSideEffect(ProfileSideEffect.ShowToast(context.getString(R.string.logout_success)))
        navigator.navigate(Route.Login)
    }

    fun onArticleLikeClicked(articleId: String) = intent {
        val currentArticles = state.recentArticles
        val updatedArticles = currentArticles.map { article ->
            if (article.id == articleId) {
                article.copy(
                    isLiked = !article.isLiked,
                    likeCount = if (article.isLiked) article.likeCount - 1 else article.likeCount + 1
                )
            } else {
                article
            }
        }
        reduce { state.copy(recentArticles = updatedArticles) }
    }

    private fun loadProfileData() = intent {
        reduce { state.copy(isLoading = true) }

        try {
            // TODO: 실제 API 호출로 교체
            val mockRecentArticles = listOf(
                RecentArticle(
                    id = "1",
                    title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                    timestamp = "P.51",
                    likeCount = 1247,
                    isLiked = false
                ), RecentArticle(
                    id = "2",
                    title = "이젠 더이상 뒤돌지도 않아. 왜지, 왜 나는 이렇게 말라가는 거지.",
                    timestamp = "P.51",
                    likeCount = 856,
                    isLiked = true
                )
            )

            reduce {
                state.copy(
                    isLoading = false,
                    userName = "박성준",
                    publishedArticleCount = 24,
                    likedArticleCount = 8,
                    recentArticles = mockRecentArticles,
                    profileImageUrl = "https://example.com/profile.jpg" // 실제 이미지 URL
                )
            }
        } catch (e: Exception) {
            reduce { state.copy(isLoading = false) }
            postSideEffect(
                ProfileSideEffect.ShowError(
                    context.getString(R.string.error_load_profile_failed)
                )
            )
        }
    }
}