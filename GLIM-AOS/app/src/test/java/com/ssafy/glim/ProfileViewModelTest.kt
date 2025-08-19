package com.ssafy.glim

import android.util.Log
import com.ssafy.glim.core.domain.model.QuoteSummary
import com.ssafy.glim.core.domain.model.user.Gender
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.model.user.UserStatus
import com.ssafy.glim.core.domain.usecase.quote.GetMyLikedQuoteUseCase
import com.ssafy.glim.core.domain.usecase.quote.GetMyUploadQuoteUseCase
import com.ssafy.glim.core.domain.usecase.user.DeleteUserUseCase
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.domain.usecase.user.LogOutUseCase
import com.ssafy.glim.core.navigation.MyGlimsRoute
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.core.navigation.UpdateInfoRoute
import com.ssafy.glim.feature.profile.EditProfileDialogState
import com.ssafy.glim.feature.profile.LogoutDialogState
import com.ssafy.glim.feature.profile.ProfileSideEffect
import com.ssafy.glim.feature.profile.ProfileViewModel
import com.ssafy.glim.feature.profile.WithdrawalDialogState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {

    private val navigator = mockk<Navigator>(relaxed = true)
    private val getUserByIdUseCase = mockk<GetUserByIdUseCase>()
    private val deleteUserUseCase = mockk<DeleteUserUseCase>()
    private val getMyUploadQuoteUseCase = mockk<GetMyUploadQuoteUseCase>()
    private val getMyLikedQuoteUseCase = mockk<GetMyLikedQuoteUseCase>()
    private val logOutUseCase = mockk<LogOutUseCase>()

    private lateinit var vm: ProfileViewModel

    @Before
    fun setUp() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        unmockkStatic(Log::class)
    }

    private fun newVm() {
        vm = ProfileViewModel(
            navigator = navigator,
            getUserByIdUseCase = getUserByIdUseCase,
            deleteUserUseCase = deleteUserUseCase,
            getMyUploadQuoteUseCase = getMyUploadQuoteUseCase,
            getMyLikedQuoteUseCase = getMyLikedQuoteUseCase,
            logOutUseCase = logOutUseCase
        )
    }

    // ---------- Navigation intents ----------

    @Test
    fun navigateToGlimsLiked_navigates() = runTest {
        newVm()
        vm.navigateToGlimsLiked()
        coVerify { navigator.navigate(MyGlimsRoute.Liked) }
    }

    @Test
    fun navigateToGlimsUpload_navigates() = runTest {
        newVm()
        vm.navigateToGlimsUpload()
        coVerify { navigator.navigate(MyGlimsRoute.Upload) }
    }

    @Test
    fun navigateToPersonalInfo_hidesDialog_and_navigates() = runTest {
        newVm()
        vm.test(this) {
            vm.navigateToEditProfile()
            awaitState().apply { assertEquals(EditProfileDialogState.Showing, editProfileDialogState) }

            vm.navigateToPersonalInfo()
            awaitState().apply { assertEquals(EditProfileDialogState.Hidden, editProfileDialogState) }
        }
        coVerify { navigator.navigate(UpdateInfoRoute.Personal) }
    }

    @Test
    fun navigateToPasswordChange_hidesDialog_and_navigates() = runTest {
        newVm()
        vm.test(this) {
            vm.navigateToEditProfile()
            awaitState().apply { assertEquals(EditProfileDialogState.Showing, editProfileDialogState) }

            vm.navigateToPasswordChange()
            awaitState().apply { assertEquals(EditProfileDialogState.Hidden, editProfileDialogState) }
        }
        coVerify { navigator.navigate(UpdateInfoRoute.Password) }
    }

    @Test
    fun navigateToSettings_navigates() = runTest {
        newVm()
        vm.navigateToSettings()
        coVerify { navigator.navigate(Route.Setting) }
    }

    // ---------- Edit Profile dialog ----------

    @Test
    fun editProfile_open_and_cancel() = runTest {
        newVm()
        vm.test(this) {
            vm.navigateToEditProfile()
            awaitState().apply { assertEquals(EditProfileDialogState.Showing, editProfileDialogState) }

            vm.onEditProfileDialogCancel()
            awaitState().apply { assertEquals(EditProfileDialogState.Hidden, editProfileDialogState) }
        }
    }

    // ---------- loadProfileData ----------

    @Test
    fun loadProfileData_success_updates_state() = runTest {
        val user = User(
            id = 1L,
            email = "e@e.com",
            nickname = "홍길동",
            birthDate = "1990-01-01",
            gender = Gender.MALE,
            status = UserStatus.ACTIVE,
            profileUrl = "url.jpg"
        )
        val uploads = listOf(
            QuoteSummary("c1", "10", 1L, 100L, "B1", 2L, false, "2022-01-01"),
            QuoteSummary("c2", "11", 2L, 200L, "B2", 9L, true, "2022-01-02")
        )
        val liked = listOf(
            QuoteSummary("lc1", "111", 11L, 111L, "LB1", 19L, true, "2021-01-01")
        )

        coEvery { getUserByIdUseCase() } returns user
        coEvery { getMyUploadQuoteUseCase() } returns uploads
        coEvery { getMyLikedQuoteUseCase() } returns liked

        newVm()
        vm.test(this) {
            vm.loadProfileData()

            // isRefreshing=true가 먼저 올 수 있음 → 하나 소비
            awaitState().apply { assertTrue(isRefreshing) }

            // 비동기 완료
            advanceUntilIdle()

            // 최종 상태 검증
            awaitState().apply {
                assertEquals("홍길동", userName)
                assertEquals("url.jpg", profileImageUrl)
                assertEquals(uploads.size, publishedGlimCount)
                assertEquals(liked.size, likedGlimCount)
                assertEquals(uploads, uploadQuotes)
                assertFalse(isRefreshing)
                assertFalse(error)
            }
        }
    }

    @Test
    fun loadProfileData_failure_sets_error_and_posts_side_effect() = runTest {
        // 실패는 catch로 진입해야 사이드이펙트가 발생하므로 예외를 던지게 스텁
        coEvery { getUserByIdUseCase() } throws RuntimeException("boom")
        coEvery { getMyUploadQuoteUseCase() } returns emptyList()
        coEvery { getMyLikedQuoteUseCase() } returns emptyList()

        newVm()
        vm.test(this) {
            vm.loadProfileData()

            // 시작 상태 먼저 소비
            awaitState().apply { assertTrue(isRefreshing) }

            // 비동기 완료
            advanceUntilIdle()

            // 최종 상태 먼저 소비
            awaitState().apply {
                assertEquals("", userName)
                assertNull(profileImageUrl)
                assertEquals(0, publishedGlimCount)
                assertEquals(0, likedGlimCount)
                assertTrue(uploadQuotes.isEmpty())
                assertFalse(isRefreshing)
                assertTrue(error)
            }

            // 그 다음 사이드이펙트 소비 → 순서가 바뀌면 Timeout 날 수 있음
            expectSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
        }
    }

    // ---------- Logout ----------

    @Test
    fun onLogOutClick_sets_confirmation() = runTest {
        newVm()
        vm.test(this) {
            vm.onLogOutClick()
            awaitState().apply { assertEquals(LogoutDialogState.Confirmation, logoutDialogState) }
        }
    }

    @Test
    fun onLogoutConfirm_success_posts_success_and_hides_dialog() = runTest {
        coEvery { logOutUseCase() } returns Unit

        newVm()
        vm.test(this) {
            vm.onLogOutClick()
            awaitState().apply { assertEquals(LogoutDialogState.Confirmation, logoutDialogState) }

            vm.onLogoutConfirm()
            awaitState().apply { assertEquals(LogoutDialogState.Processing, logoutDialogState) }

            // 사이드이펙트 먼저
            expectSideEffect(ProfileSideEffect.ShowError(R.string.logout_success))
            // 최종 상태
            awaitState().apply { assertEquals(LogoutDialogState.Hidden, logoutDialogState) }
        }
    }

    @Test
    fun onLogoutConfirm_failure_posts_failed_and_hides_dialog() = runTest {
        coEvery { logOutUseCase() } throws RuntimeException("fail")

        newVm()
        vm.test(this) {
            vm.onLogOutClick()
            awaitState().apply { assertEquals(LogoutDialogState.Confirmation, logoutDialogState) }

            vm.onLogoutConfirm()
            awaitState().apply { assertEquals(LogoutDialogState.Processing, logoutDialogState) }

            // 실패 사이드이펙트 먼저
            expectSideEffect(ProfileSideEffect.ShowError(R.string.logout_failed))
            // 최종 상태
            awaitState().apply { assertEquals(LogoutDialogState.Hidden, logoutDialogState) }
        }
    }

    @Test
    fun onLogoutCancel_hides_dialog() = runTest {
        newVm()
        vm.test(this) {
            vm.onLogOutClick()
            awaitState().apply { assertEquals(LogoutDialogState.Confirmation, logoutDialogState) }

            vm.onLogoutCancel()
            awaitState().apply { assertEquals(LogoutDialogState.Hidden, logoutDialogState) }
        }
    }

    // ---------- Withdrawal (탈퇴) ----------

    @Test
    fun withdrawal_flow_warning_to_confirmation() = runTest {
        newVm()
        vm.test(this) {
            vm.onWithdrawalClick()
            awaitState().apply { assertEquals(WithdrawalDialogState.Warning, withdrawalDialogState) }

            vm.onWarningConfirm()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Confirmation, withdrawalDialogState)
                assertEquals(10, countdownSeconds)
            }

            // 10..0 도달: 총 11초 필요
            advanceTimeBy(11_000)
            advanceUntilIdle()

            // 마지막 상태만 소비
            awaitState().apply { assertEquals(0, countdownSeconds) }
        }
    }

    @Test
    fun onFinalConfirm_only_runs_when_text_matches_and_countdown_zero() = runTest {
        coEvery { deleteUserUseCase() } returns Unit

        newVm()
        vm.test(this) {
            // 조건 미충족: 변화 없음
            vm.onFinalConfirm()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Hidden, withdrawalDialogState)
                assertEquals("", userInputText)
                assertEquals(0, countdownSeconds)
                assertFalse(isWithdrawalLoading)
            }

            // 정상 플로우
            vm.onWithdrawalClick()
            awaitState()
            vm.onWarningConfirm()
            // onWarningConfirm 직후 상태(Confirmation, 10)
            awaitState().apply { assertEquals(10, countdownSeconds) }

            vm.onUserInputChanged("탈퇴하겠습니다")
            awaitState().apply { assertEquals("탈퇴하겠습니다", userInputText) }

            // 카운트다운 완료
            advanceTimeBy(11_000)
            advanceUntilIdle()
            awaitState().apply { assertEquals(0, countdownSeconds) }

            vm.onFinalConfirm()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Processing, withdrawalDialogState)
                assertTrue(isWithdrawalLoading)
            }

            // 성공 사이드이펙트
            expectSideEffect(ProfileSideEffect.ShowError(R.string.withdrawal_success))
            // 최종 상태
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Hidden, withdrawalDialogState)
                assertFalse(isWithdrawalLoading)
                assertEquals("", userInputText)
                assertEquals(0, countdownSeconds)
            }
        }

        coVerify { navigator.navigateAndClearBackStack(Route.Login) }
    }

    @Test
    fun onFinalConfirm_failure_emits_failed_and_resets_dialog() = runTest {
        coEvery { deleteUserUseCase() } throws RuntimeException("fail")

        newVm()
        vm.test(this) {
            vm.onWithdrawalClick()
            awaitState()
            vm.onWarningConfirm()
            awaitState().apply { assertEquals(10, countdownSeconds) }
            vm.onUserInputChanged("탈퇴하겠습니다")
            awaitState()

            // 카운트다운 완료
            advanceTimeBy(11_000)
            advanceUntilIdle()
            awaitState().apply { assertEquals(0, countdownSeconds) }

            vm.onFinalConfirm()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Processing, withdrawalDialogState)
                assertTrue(isWithdrawalLoading)
            }

            // 실패 사이드이펙트
            expectSideEffect(ProfileSideEffect.ShowError(R.string.withdrawal_failed))
            // 최종 상태
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Hidden, withdrawalDialogState)
                assertFalse(isWithdrawalLoading)
                assertEquals("", userInputText)
                assertEquals(0, countdownSeconds)
            }
        }
    }

    @Test
    fun onWarningCancel_resets_dialog() = runTest {
        newVm()
        vm.test(this) {
            vm.onWithdrawalClick()
            awaitState().apply { assertEquals(WithdrawalDialogState.Warning, withdrawalDialogState) }

            vm.onWarningCancel()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Hidden, withdrawalDialogState)
                assertEquals("", userInputText)
                assertEquals(0, countdownSeconds)
            }
        }
    }

    @Test
    fun onFinalCancel_resets_dialog() = runTest {
        newVm()
        vm.test(this) {
            vm.onWithdrawalClick()
            awaitState()
            vm.onWarningConfirm()
            awaitState().apply { assertEquals(10, countdownSeconds) }

            // 카운트다운 진행 중에도 즉시 리셋되어야 함
            vm.onFinalCancel()
            awaitState().apply {
                assertEquals(WithdrawalDialogState.Hidden, withdrawalDialogState)
                assertEquals("", userInputText)
                assertEquals(0, countdownSeconds)
            }
        }
    }

    @Test
    fun onUserInputChanged_updates_text() = runTest {
        newVm()
        vm.test(this) {
            vm.onUserInputChanged("abc")
            awaitState().apply { assertEquals("abc", userInputText) }
        }
    }
}
