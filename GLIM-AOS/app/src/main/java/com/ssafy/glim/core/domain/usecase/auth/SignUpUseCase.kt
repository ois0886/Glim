package com.ssafy.glim.core.domain.usecase.auth

import android.graphics.Bitmap
import com.ssafy.glim.core.domain.repository.AuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: List<Int>
    ) = repository.signUp(email, nickname, password, gender, birthDate)
}
