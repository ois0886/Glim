package com.ssafy.glim.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.R
import com.ssafy.glim.core.common.extensions.formatGenderToString
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.domain.model.user.Gender
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.model.user.UserStatus
import com.ssafy.glim.core.domain.usecase.user.GetUserByIdUseCase
import com.ssafy.glim.core.domain.usecase.user.UpdateUserUseCase
import com.ssafy.glim.core.util.DefaultImageUtils
import com.ssafy.glim.feature.update.UpdateInfoSideEffect
import com.ssafy.glim.feature.update.UpdateInfoUiState
import com.ssafy.glim.feature.update.UpdateType
import com.ssafy.glim.feature.update.UpdateViewModel
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.orbitmvi.orbit.test.test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UpdateViewModelTest {

    private val getUserByIdUseCase: GetUserByIdUseCase = mockk()
    private val updateUserUseCase: UpdateUserUseCase = mockk()
    private lateinit var viewModel: UpdateViewModel

    private val user = User(
        id = 1L,
        email = "user@email.com",
        nickname = "닉네임",
        birthDate = "19900101",
        gender = Gender.MALE,
        status = UserStatus.ACTIVE,
        profileUrl = "http://profile.img"
    )


    @Before
    fun setUp() {
        mockkObject(ValidationUtils)
        mockkObject(DefaultImageUtils)
        // 1. uriToBitmap은 coEvery로 mock
        coEvery { DefaultImageUtils.uriToBitmap(any(), any()) } returns mockk<Bitmap>(relaxed = true)
        // 2. downloadImageFromUrl (ViewModel 내 suspend function)도 반드시 mock! (아래 참고)
        // 3. decodeResource도 mock 필요 (BitmapFactory 리턴값 stub)
        mockkStatic(BitmapFactory::class)
        every { BitmapFactory.decodeResource(any(), any()) } returns mockk<Bitmap>(relaxed = true)
        // (테스트에서 실제 network를 부르지 않음, 실패해도 null만 리턴되면 기본 이미지로 대체됨)
        viewModel = spyk(UpdateViewModel(getUserByIdUseCase, updateUserUseCase)) {
            // downloadImageFromUrl까지 suspend stub 필요 시 실제 구현에서 오버라이드
            coEvery { downloadImageFromUrl(any()) } returns mockk<Bitmap>(relaxed = true)
        }
    }

    @Test
    fun getUseCurrentInfo_success_shouldUpdateState() = runTest {
        coEvery { getUserByIdUseCase() } returns user

        viewModel.test(this) {
            containerHost.getUseCurrentInfo()
            awaitState()
            val state = awaitState()
            assertEquals(false, state.isLoading)
            assertEquals(user.nickname, state.name)
            assertEquals(user.email, state.email)
            assertEquals(user.gender.formatGenderToString(), state.gender)
            assertEquals(user.birthDate, state.birthDate)
            assertEquals(user.profileUrl, state.profileImageUri)
            assertEquals(TextFieldValue(user.nickname), state.newName)
        }
    }

    @Test
    fun getUseCurrentInfo_failure_shouldShowError_andSetLoadingFalse() = runTest {
        coEvery { getUserByIdUseCase() } throws RuntimeException("fail")
        viewModel.test(this) {
            containerHost.getUseCurrentInfo()
            awaitState()
            val state = awaitState()
            assertEquals(false, state.isLoading)
            expectSideEffect(UpdateInfoSideEffect.ShowError(R.string.error_load_profile_failed))
        }
    }

    @Test
    fun setUpdateType_shouldClearErrorsAndSetType() = runTest {
        viewModel.test(this) {
            containerHost.setUpdateType(UpdateType.PASSWORD)
            val state = awaitState()
            assertEquals(UpdateType.PASSWORD, state.updateType)
            assertNull(state.newNameError)
            assertNull(state.currentPasswordError)
            assertNull(state.newPasswordError)
            assertNull(state.confirmPasswordError)
        }
    }

    @Test
    fun onNameChanged_invalid_shouldSetError() = runTest {
        val invalidName = "!!"
        val errResId = 1234
        every {
            ValidationUtils.validateName(
                name = invalidName,
                emptyErrorRes = any(),
                invalidErrorRes = any()
            )
        } returns ValidationResult.Invalid(errResId)

        viewModel.test(this) {
            containerHost.onNameChanged(TextFieldValue(invalidName))
            val state = awaitState()
            assertEquals(TextFieldValue(invalidName), state.newName)
            assertEquals(errResId, state.newNameError)
        }
    }

    @Test
    fun onProfileImageClicked_shouldShowImagePicker() = runTest {
        viewModel.test(this) {
            containerHost.onProfileImageClicked()
            expectSideEffect(UpdateInfoSideEffect.ShowImagePicker)
        }
    }

    @Test
    fun onImageSelected_shouldSetImageUri_andIsImageSelectedTrue() = runTest {
        val uriMock = mockk<Uri>(relaxed = true)
        every { uriMock.toString() } returns "http://img"
        viewModel.test(this) {
            containerHost.onImageSelected(uriMock)
            val state = awaitState()
            assertEquals("http://img", state.profileImageUri)
            assertEquals(true, state.isImageSelected)
        }
    }

    @Test
    fun onCurrentPasswordChanged_invalid_shouldSetError() = runTest {
        val wrongCurrentPassword = "wrong"
        val errResId = 999
        every {
            ValidationUtils.validatePassword(
                password = wrongCurrentPassword,
                emptyErrorRes = any<Int>(),
                invalidErrorRes = any<Int>()
            )
        } returns ValidationResult.Invalid(errResId)

        viewModel.test(this) {
            containerHost.onCurrentPasswordChanged(TextFieldValue(wrongCurrentPassword))
            val state = awaitState()
            assertEquals(TextFieldValue(wrongCurrentPassword), state.password)
            assertEquals(errResId, state.currentPasswordError)
        }
    }

    @Test
    fun onNewPasswordChanged_invalid_shouldSetErrorAndConfirmPasswordError() = runTest {
        val wrongNewPassword = "badpw"
        val errResId = 888
        val mismatchErrResId = 777

        every {
            ValidationUtils.validatePassword(
                password = wrongNewPassword,
                emptyErrorRes = any<Int>(),
                invalidErrorRes = any<Int>()
            )
        } returns ValidationResult.Invalid(errResId)

        every {
            ValidationUtils.validatePasswordConfirm(
                password = wrongNewPassword,
                confirmPassword = "badconf",
                mismatchErrorRes = any<Int>()
            )
        } returns ValidationResult.Invalid(mismatchErrResId)

        viewModel.test(this, UpdateInfoUiState(confirmPassword = TextFieldValue("badconf"))) {
            containerHost.onNewPasswordChanged(TextFieldValue(wrongNewPassword))
            val state = awaitState()
            assertEquals(TextFieldValue(wrongNewPassword), state.newPassword)
            assertEquals(errResId, state.newPasswordError)
            assertEquals(mismatchErrResId, state.confirmPasswordError)
        }
    }

    @Test
    fun onConfirmPasswordChanged_invalid_shouldSetError() = runTest {
        val errResId = 555
        val originalPassword = "originalPW"
        val differentConfirm = "otherPW"
        every {
            ValidationUtils.validatePasswordConfirm(
                password = originalPassword,
                confirmPassword = differentConfirm,
                mismatchErrorRes = any<Int>()
            )
        } returns ValidationResult.Invalid(errResId)

        viewModel.test(this, UpdateInfoUiState(newPassword = TextFieldValue(originalPassword))) {
            containerHost.onConfirmPasswordChanged(TextFieldValue(differentConfirm))
            val state = awaitState()
            assertEquals(TextFieldValue(differentConfirm), state.confirmPassword)
            assertEquals(errResId, state.confirmPasswordError)
        }
    }
}
