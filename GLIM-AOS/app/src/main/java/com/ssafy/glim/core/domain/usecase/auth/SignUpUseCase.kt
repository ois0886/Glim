package com.ssafy.glim.core.domain.usecase.auth

import android.content.Context
import com.ssafy.glim.core.domain.repository.AuthRepository
import com.ssafy.glim.core.util.DefaultImageUtils
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(
        context: Context,
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: List<Int>
    ) {
        val defaultProfileBitmap = DefaultImageUtils.getDefaultProfileBitmap(context)
            ?: throw IllegalStateException("기본 프로필 이미지를 로드할 수 없습니다")
        repository.signUp(
            email = email,
            nickname = nickname,
            password = password,
            gender = gender,
            birthDate = birthDate,
            profileBitmap = defaultProfileBitmap
        )
    }
}
