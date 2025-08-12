package com.ssafy.glim.core.domain.usecase.user

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UpdateUserUseCase @Inject constructor(
    private val repository: UserRepository
) {
    suspend operator fun invoke(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: List<Int>,
        profileUrl: Bitmap?
    ) = repository.updateUser(
        memberId = memberId,
        password = password,
        nickname = nickname,
        gender = gender,
        birthDate = birthDate,
        profileUrl = profileUrl
    )
}
