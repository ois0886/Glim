package com.ssafy.glim.core.domain.usecase.user

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        password: String,
        nickname: String,
        gender: String,
        birthDate: String,
        profileImage: Bitmap
    ) = repository.updateUser(
        password = password,
        nickname = nickname,
        gender = gender,
        birthDate = birthDate,
        profileImage = profileImage
    )
}
