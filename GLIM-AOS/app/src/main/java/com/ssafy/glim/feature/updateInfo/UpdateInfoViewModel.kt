package com.ssafy.glim.feature.updateInfo

import android.net.Uri
import androidx.lifecycle.ViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.common.utils.ValidationResult
import com.ssafy.glim.core.common.utils.ValidationUtils
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
internal class UpdateInfoViewModel
@Inject constructor(
    private val navigator: Navigator,
    //private val updateProfileUseCase: UpdateProfileUseCase,
) : ViewModel(), ContainerHost<UpdateInfoUiState, UpdateInfoSideEffect> {

    override val container = container<UpdateInfoUiState, UpdateInfoSideEffect>(
        initialState = UpdateInfoUiState()
    )

    fun onImageSelected(uri: Uri) {
        intent {
            reduce { state.copy(profileImageUri = uri.toString()) }
        }
    }

    fun onNameChanged(name: String) = intent {
        val validationResult = ValidationUtils.validateName(
            name = name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid
        )

        val error = when (validationResult) {
            is ValidationResult.Valid -> null
            is ValidationResult.Invalid -> validationResult.errorMessageRes
        }

        reduce { state.copy(name = name, nameError = error) }
    }

    fun onProfileImageClicked() = intent {
        postSideEffect(UpdateInfoSideEffect.ShowImagePicker)
    }

    fun onSaveClicked() = intent {
        val nameValidation = ValidationUtils.validateName(
            name = state.name,
            emptyErrorRes = R.string.error_name_empty,
            invalidErrorRes = R.string.error_name_invalid
        )

        val nameError = if (nameValidation is ValidationResult.Invalid) {
            nameValidation.errorMessageRes
        } else null

        if (nameError != null) {
            return@intent
        }

        reduce { state.copy(isLoading = true) }
        delay(1_000)
        reduce { state.copy(isLoading = false) }
    }

    fun onBackClicked() = intent {
        navigator.navigateBack()
    }
}