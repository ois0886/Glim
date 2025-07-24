package com.ssafy.glim.feature.updateInfo

import androidx.annotation.StringRes

data class UpdateInfoUiState(
    val name: String = "",
    val email: String = "",
    val profileImageUri: String? = null,
    @StringRes val nameError: Int? = null,
    val isLoading: Boolean = false,
) {
    val isSaveEnabled: Boolean
        get() = nameError == null &&
            name.isNotBlank()
}

sealed interface UpdateInfoSideEffect {
    data class ShowErrorRes(@StringRes val messageRes: Int) : UpdateInfoSideEffect
    data object ShowImagePicker : UpdateInfoSideEffect
    data object ProfileUpdated : UpdateInfoSideEffect
}
