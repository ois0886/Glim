package com.ssafy.glim.viewmodel

import android.util.Log
import com.ssafy.glim.R
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
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

    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    private val navigator = mockk<Navigator>(relaxed = true)
    private val getUserByIdUseCase = mockk<GetUserByIdUseCase>()
    private val deleteUserUseCase = mockk<DeleteUserUseCase>()
    private val getMyUploadQuoteUseCase = mockk<GetMyUploadQuoteUseCase>()
    private val getMyLikedQuoteUseCase = mockk<GetMyLikedQuoteUseCase>()
    private val logOutUseCase = mockk<LogOutUseCase>()

    private lateinit var viewModel: ProfileViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    private fun createViewModel() {
        viewModel = ProfileViewModel(
            navigator = navigator,
            getUserByIdUseCase = getUserByIdUseCase,
            deleteUserUseCase = deleteUserUseCase,
            getMyUploadQuoteUseCase = getMyUploadQuoteUseCase,
            getMyLikedQuoteUseCase = getMyLikedQuoteUseCase,
            logOutUseCase = logOutUseCase
        )
    }

    // -------- 네비게이션 --------

    @Test
    fun 글림스_좋아요_탭으로_이동() = runTest(dispatcher) {
        createViewModel()
        viewModel.navigateToGlimsLiked()
        advanceUntilIdle()
        coVerify { navigator.navigate(MyGlimsRoute.Liked) }
    }

    @Test
    fun 글림스_업로드_탭으로_이동() = runTest(dispatcher) {
        createViewModel()
        viewModel.navigateToGlimsUpload()
        advanceUntilIdle()
        coVerify { navigator.navigate(MyGlimsRoute.Upload) }
    }

    @Test
    fun 개인정보수정으로_이동하면_편집다이얼로그숨김() = runTest(dispatcher) {
        createViewModel()
        viewModel.test(this) {
            viewModel.navigateToEditProfile()
            awaitState().apply {
                assertEquals(
                    EditProfileDialogState.Showing,
                    editProfileDialogState
                )
            }

            viewModel.navigateToPersonalInfo()
            awaitState().apply {
                assertEquals(
                    EditProfileDialogState.Hidden,
                    editProfileDialogState
                )
            }
        }
        coVerify { navigator.navigate(UpdateInfoRoute.Personal) }
    }

    @Test
    fun 비밀번호변경으로_이동하면_편집다이얼로그숨김() = runTest(dispatcher) {
        createViewModel()
        viewModel.test(this) {
            viewModel.navigateToEditProfile()
            awaitState().apply {
                assertEquals(
                    EditProfileDialogState.Showing,
                    editProfileDialogState
                )
            }

            viewModel.navigateToPasswordChange()
            awaitState().apply {
                assertEquals(
                    EditProfileDialogState.Hidden,
                    editProfileDialogState
                )
            }
        }
        coVerify { navigator.navigate(UpdateInfoRoute.Password) }
    }

    @Test
    fun 설정화면으로_이동() = runTest(dispatcher) {
        createViewModel()
        viewModel.navigateToSettings()
        advanceUntilIdle()
        coVerify { navigator.navigate(Route.Setting) }
    }

    // ---------- 프로필 로드 ----------

    @Test
    fun 프로필_로드_성공시_상태업데이트() = runTest(dispatcher) {
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
        val liked = listOf(QuoteSummary("lc1", "111", 11L, 111L, "LB1", 19L, true, "2021-01-01"))

        coEvery { getUserByIdUseCase() } returns user
        coEvery { getMyUploadQuoteUseCase() } returns uploads
        coEvery { getMyLikedQuoteUseCase() } returns liked

        createViewModel()
        viewModel.test(this) {
            viewModel.loadProfileData()
            awaitState() // isRefreshing true
            advanceUntilIdle()

            val s = awaitState()
            assertEquals("홍길동", s.userName)
            assertEquals("url.jpg", s.profileImageUrl)
            assertEquals(2, s.publishedGlimCount)
            assertEquals(1, s.likedGlimCount)
            assertEquals(uploads, s.uploadQuotes)
            assertFalse(s.error)
            assertFalse(s.isRefreshing)
        }
    }

    @Test
    fun 프로필_로드_실패시_에러상태_및_사이드이펙트() = runTest(dispatcher) {
        // 실패를 유도해서 catch 블록과 사이드이펙트 방출을 검증
        coEvery { getUserByIdUseCase() } throws RuntimeException("boom")
        coEvery { getMyUploadQuoteUseCase() } returns emptyList()
        coEvery { getMyLikedQuoteUseCase() } returns emptyList()

        createViewModel()
        viewModel.test(this) {
            viewModel.loadProfileData()

            // 1) 시작 상태: isRefreshing=true
            awaitState().apply { assertTrue(isRefreshing) }

            // 2) 내부 코루틴 완료
            advanceUntilIdle()

            // 3) 최종 에러 상태
            awaitState().apply {
                assertEquals("", userName)
                assertNull(profileImageUrl)
                assertEquals(0, publishedGlimCount)
                assertEquals(0, likedGlimCount)
                assertTrue(uploadQuotes.isEmpty())
                assertFalse(isRefreshing)
                assertTrue(error)
            }

            // 4) 사이드이펙트
            expectSideEffect(ProfileSideEffect.ShowError(R.string.error_load_profile_failed))
        }
    }

    // ---------- 로그아웃 ----------

    @Test
    fun 로그아웃_확인창_열림() = runTest(dispatcher) {
        createViewModel()
        viewModel.test(this) {
            viewModel.onLogOutClick()
            awaitState().apply { assertEquals(LogoutDialogState.Confirmation, logoutDialogState) }
        }
    }

    @Test
    fun 로그아웃_성공시_사이드이펙트와_다이얼로그숨김() = runTest(dispatcher) {
        coEvery { logOutUseCase() } returns Unit

        createViewModel()
        viewModel.test(this) {
            viewModel.onLogOutClick()
            awaitState()
            viewModel.onLogoutConfirm()
            awaitState() // Processing

            expectSideEffect(ProfileSideEffect.ShowError(R.string.logout_success))
            awaitState().apply { assertEquals(LogoutDialogState.Hidden, logoutDialogState) }
        }
    }

    @Test
    fun 탈퇴플로우_경고에서_카운트다운_진행() = runTest(dispatcher) {
        createViewModel()
        viewModel.test(this) {
            viewModel.onWithdrawalClick()
            awaitState().apply {
                assertEquals(
                    WithdrawalDialogState.Warning,
                    withdrawalDialogState
                )
            }

            viewModel.onWarningConfirm()
            awaitState().apply { assertEquals(10, countdownSeconds) }

            repeat(10) { t ->
                advanceTimeBy(1000)
                awaitState().apply { assertEquals(9 - t, countdownSeconds.coerceAtLeast(0)) }
            }
        }
    }

    // 👇 여기에 onFinalConfirm, 실패/성공 테스트도 같은 흐름 (첫 10 소비 후 카운트다운 + 최종 확인 시 expectSideEffect → 마지막 상태 확인)
}
