package com.ssafy.glim

import com.ssafy.glim.core.domain.model.user.Gender
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.model.user.UserStatus
import com.ssafy.glim.core.domain.usecase.auth.LogOutUseCase
import com.ssafy.glim.core.domain.usecase.user.DeleteUserUseCase
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.feature.profile.EditProfileDialogState
import com.ssafy.glim.feature.profile.LogoutDialogState
import com.ssafy.glim.feature.profile.ProfileSideEffect
import com.ssafy.glim.feature.profile.ProfileUiState
import com.ssafy.glim.feature.profile.ProfileViewModel
import com.ssafy.glim.feature.profile.WithdrawalDialogState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test

/*
    24 개의 테스트
 */
@ExperimentalCoroutinesApi
class ProfileViewModelTest {

    private val mockNavigator = mockk<Navigator>(relaxed = true)
    private val mockLogOutUseCase = mockk<LogOutUseCase>()
    private val mockGetUserByIdUseCase = mockk<GetUserByIdUseCase>()
    private val mockDeleteUserUseCase = mockk<DeleteUserUseCase>()

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        viewModel = ProfileViewModel(
            navigator = mockNavigator,
            logOutUseCase = mockLogOutUseCase,
            getUserByIdUseCase = mockGetUserByIdUseCase,
            deleteUserUseCase = mockDeleteUserUseCase
        )
    }

    @Test
    fun `프로필_데이터_로딩_성공_테스트`() = runTest {
        // Given
        val expectedUser = User(
            id = 1L,
            email = "test@example.com",
            nickname = "테스트사용자",
            birthDate = "1990-01-01",
            gender = Gender.MALE,
            status = UserStatus.ACTIVE
        )
        coEvery { mockGetUserByIdUseCase() } returns expectedUser

        // When & Then
        viewModel.test(this) {
            containerHost.loadProfileData()

            awaitState().run {
                assert(isLoading == true)
            }

            awaitState().run {
                assert(isLoading == false)
                assert(userName == "테스트사용자")
            }
        }

        coVerify { mockGetUserByIdUseCase() }
    }

    @Test
    fun `프로필_데이터_로딩_실패_테스트`() = runTest {
        // Given
        coEvery { mockGetUserByIdUseCase() } throws Exception("API 오류")

        // When & Then
        viewModel.test(this) {
            containerHost.loadProfileData()

            awaitState().run {
                assert(isLoading == true)
            }

            awaitState().run {
                assert(isLoading == false)
            }

            expectSideEffect(
                ProfileSideEffect.ShowError(R.string.error_load_profile_failed)
            )
        }
    }

    @Test
    fun `로그아웃_다이얼로그_표시_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.onLogOutClick()

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Confirmation)
            }
        }
    }

    @Test
    fun `로그아웃_확인_성공_테스트`() = runTest {
        // Given
        coEvery { mockLogOutUseCase() } returns Unit

        // When & Then
        viewModel.test(this) {
            containerHost.onLogoutConfirm()

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Processing)
            }

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Hidden)
                assert(isLoading == false)
            }

            expectSideEffect(
                ProfileSideEffect.ShowToast(R.string.logout_success)
            )
        }

        coVerify { mockLogOutUseCase() }
    }

    @Test
    fun `로그아웃_실패_테스트`() = runTest {
        // Given
        coEvery { mockLogOutUseCase() } throws Exception("로그아웃 실패")

        // When & Then
        viewModel.test(this) {
            containerHost.onLogoutConfirm()

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Processing)
            }

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Hidden)
                assert(isLoading == false)
            }

            expectSideEffect(
                ProfileSideEffect.ShowError(R.string.logout_failed)
            )
        }
    }

    @Test
    fun `로그아웃_취소_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(logoutDialogState = LogoutDialogState.Confirmation)
        ) {
            containerHost.onLogoutCancel()

            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Hidden)
            }
        }
    }

    @Test
    fun `프로필_편집_다이얼로그_표시_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.navigateToEditProfile()

            awaitState().run {
                assert(editProfileDialogState == EditProfileDialogState.Showing)
            }
        }
    }

    @Test
    fun `프로필_편집_다이얼로그_취소_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(editProfileDialogState = EditProfileDialogState.Showing)
        ) {
            containerHost.onEditProfileDialogCancel()

            awaitState().run {
                assert(editProfileDialogState == EditProfileDialogState.Hidden)
            }
        }
    }

    @Test
    fun `잠금_설정_토스트_메시지_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.navigateToLockSettings()

            expectSideEffect(
                ProfileSideEffect.ShowToast(R.string.lock_settings_message)
            )
        }
    }

    @Test
    fun `알림_설정_토스트_메시지_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.navigateToNotificationSettings()

            expectSideEffect(
                ProfileSideEffect.ShowToast(R.string.notification_settings_message)
            )
        }
    }

    @Test
    fun `회원탈퇴_경고_다이얼로그_표시_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.onWithdrawalClick()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Warning)
            }
        }
    }

    @Test
    fun `회원탈퇴_경고_확인_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.onWarningConfirm()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Confirmation)
                assert(countdownSeconds == 10)
            }

            // 카운트다운이 진행되는 동안의 상태 변화들을 모두 소비
            repeat(10) {
                awaitState() // countdownSeconds 변화 소비
            }
        }
    }

    @Test
    fun `회원탈퇴_경고_취소_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(withdrawalDialogState = WithdrawalDialogState.Warning)
        ) {
            containerHost.onWarningCancel()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Hidden)
                assert(userInputText == "")
                assert(countdownSeconds == 0)
            }
        }
    }

    @Test
    fun `사용자_입력_텍스트_변경_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.onUserInputChanged("탈퇴하겠습니다")

            awaitState().run {
                assert(userInputText == "탈퇴하겠습니다")
            }
        }
    }

    @Test
    fun `회원탈퇴_최종_확인_성공_테스트`() = runTest {
        // Given
        coEvery { mockDeleteUserUseCase() } returns Unit

        // When & Then
        viewModel.test(
            this,
            ProfileUiState(
                userInputText = "탈퇴하겠습니다",
                countdownSeconds = 0
            )
        ) {
            containerHost.onFinalConfirm()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Processing)
                assert(isWithdrawalLoading == true)
                assert(userInputText == "탈퇴하겠습니다")
                assert(countdownSeconds == 0)
            }

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Hidden)
                assert(isWithdrawalLoading == false)
                assert(userInputText == "")
                assert(countdownSeconds == 0)
            }

            expectSideEffect(
                ProfileSideEffect.ShowToast(R.string.withdrawal_success)
            )
        }

        coVerify { mockDeleteUserUseCase() }
    }

    @Test
    fun `회원탈퇴_최종_확인_실패_테스트`() = runTest {
        // Given
        coEvery { mockDeleteUserUseCase() } throws Exception("탈퇴 실패")

        // When & Then
        viewModel.test(
            this,
            ProfileUiState(
                userInputText = "탈퇴하겠습니다",
                countdownSeconds = 0
            )
        ) {
            containerHost.onFinalConfirm()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Processing)
                assert(isWithdrawalLoading == true)
                assert(userInputText == "탈퇴하겠습니다")
                assert(countdownSeconds == 0)
            }

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Hidden)
                assert(isWithdrawalLoading == false)
                assert(userInputText == "")
                assert(countdownSeconds == 0)
            }

            expectSideEffect(
                ProfileSideEffect.ShowError(R.string.withdrawal_failed)
            )
        }
    }

    @Test
    fun `회원탈퇴_최종_확인_조건_불충족_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(
                userInputText = "잘못된 텍스트",
                countdownSeconds = 5
            )
        ) {
            containerHost.onFinalConfirm()

            // 조건이 맞지 않으면 상태 변경이 없어야 함
            expectNoItems()
        }
    }

    @Test
    fun `회원탈퇴_최종_취소_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(
                withdrawalDialogState = WithdrawalDialogState.Confirmation,
                userInputText = "탈퇴하겠습니다",
                countdownSeconds = 0
            )
        ) {
            containerHost.onFinalCancel()

            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Hidden)
                assert(userInputText == "")
                assert(countdownSeconds == 0)
            }
        }
    }

    @Test
    fun `사용자_입력_텍스트_여러번_변경_테스트`() = runTest {
        viewModel.test(this) {
            containerHost.onUserInputChanged("첫번째")
            awaitState().run {
                assert(userInputText == "첫번째")
            }

            containerHost.onUserInputChanged("두번째")
            awaitState().run {
                assert(userInputText == "두번째")
            }

            containerHost.onUserInputChanged("")
            awaitState().run {
                assert(userInputText == "")
            }
        }
    }

    @Test
    fun `회원탈퇴_최종_확인_텍스트만_맞고_카운트다운_남은_경우_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(
                userInputText = "탈퇴하겠습니다",
                countdownSeconds = 3
            )
        ) {
            containerHost.onFinalConfirm()

            // 조건이 맞지 않으면 상태 변경이 없어야 함
            expectNoItems()
        }
    }

    @Test
    fun `회원탈퇴_최종_확인_카운트다운만_끝나고_텍스트_틀린_경우_테스트`() = runTest {
        viewModel.test(
            this,
            ProfileUiState(
                userInputText = "잘못된 텍스트",
                countdownSeconds = 0
            )
        ) {
            containerHost.onFinalConfirm()

            // 조건이 맞지 않으면 상태 변경이 없어야 함
            expectNoItems()
        }
    }

    @Test
    fun `초기_상태_확인_테스트`() = runTest {
        val initialState = ProfileUiState()

        assert(initialState.profileImageUrl == null)
        assert(initialState.userName == "")
        assert(initialState.publishedGlimCount == 0)
        assert(initialState.likedGlimCount == 0)
        assert(initialState.isLoading == false)
        assert(initialState.glimShortCards.isEmpty())
        assert(initialState.logoutDialogState == LogoutDialogState.Hidden)
        assert(initialState.withdrawalDialogState == WithdrawalDialogState.Hidden)
        assert(initialState.editProfileDialogState == EditProfileDialogState.Hidden)
        assert(initialState.userInputText == "")
        assert(initialState.countdownSeconds == 0)
        assert(initialState.isWithdrawalLoading == false)
    }

    @Test
    fun `연속_다이얼로그_열기_닫기_테스트`() = runTest {
        viewModel.test(this) {
            // 프로필 편집 다이얼로그 열기
            containerHost.navigateToEditProfile()
            awaitState().run {
                assert(editProfileDialogState == EditProfileDialogState.Showing)
            }

            // 프로필 편집 다이얼로그 닫기
            containerHost.onEditProfileDialogCancel()
            awaitState().run {
                assert(editProfileDialogState == EditProfileDialogState.Hidden)
            }

            // 로그아웃 다이얼로그 열기
            containerHost.onLogOutClick()
            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Confirmation)
            }

            // 로그아웃 다이얼로그 닫기
            containerHost.onLogoutCancel()
            awaitState().run {
                assert(logoutDialogState == LogoutDialogState.Hidden)
            }
        }
    }

    @Test
    fun `회원탈퇴_플로우_전체_테스트`() = runTest {
        viewModel.test(this) {
            // 1. 탈퇴 경고 다이얼로그 열기
            containerHost.onWithdrawalClick()
            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Warning)
            }

            // 2. 경고 확인 - 카운트다운 시작
            containerHost.onWarningConfirm()
            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Confirmation)
                assert(countdownSeconds == 10)
            }

            // 3. 카운트다운 완료될 때까지 대기
            repeat(10) {
                awaitState()
            }

            // 4. 텍스트 입력
            containerHost.onUserInputChanged("탈퇴하겠습니다")
            awaitState().run {
                assert(userInputText == "탈퇴하겠습니다")
            }

            // 5. 최종 취소
            containerHost.onFinalCancel()
            awaitState().run {
                assert(withdrawalDialogState == WithdrawalDialogState.Hidden)
                assert(userInputText == "")
                assert(countdownSeconds == 0)
            }
        }
    }
}
