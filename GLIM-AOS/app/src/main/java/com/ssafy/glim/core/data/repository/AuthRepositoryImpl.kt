package com.ssafy.glim.core.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.datasource.remote.AuthRemoteDataSource
import com.ssafy.glim.core.data.datasource.remote.FcmRemoteDataSource
import com.ssafy.glim.core.data.datastore.DeviceDataStore
import com.ssafy.glim.core.data.dto.request.FcmRequest
import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.data.dto.request.VerifyEmailRequest
import com.ssafy.glim.core.data.extensions.toImagePart
import com.ssafy.glim.core.data.extensions.toJsonRequestBody
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authDataSource: AuthRemoteDataSource,
    private val authManager: AuthManager,
    private val deviceDataStore: DeviceDataStore,
    private val fcmRemoteDataSource: FcmRemoteDataSource
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        nickname: String,
        password: String,
        gender: String,
        birthDate: List<Int>,
        profileBitmap: Bitmap
    ) {
        val request = SignUpRequest(
            email = email,
            nickname = nickname,
            password = password,
            gender = gender,
            birthDate = birthDate,
        )

        val jsonRequestBody = request.toJsonRequestBody()

        val profileImagePart = profileBitmap.toImagePart(
            partName = "profileImage",
            fileName = "profile_${System.currentTimeMillis()}.jpg",
            quality = 85
        ) ?: throw IllegalStateException("프로필 이미지 변환에 실패했습니다")

        authDataSource.signUp(
            request = jsonRequestBody,
            profileImage = profileImagePart
        )
    }

    override suspend fun login(email: String, password: String) {
        val fcmToken = FirebaseMessaging.getInstance().token.await()
        val deviceId = deviceDataStore.getDeviceId()

        val response = runCatching {
            authDataSource.login(LoginRequest(email = email, password = password))
        }.onFailure { exception ->
            Log.d("AuthRepositoryImpl", "login failed: ${exception.message}")
        }.getOrThrow()

        authManager.saveToken(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken
        )
        authManager.saveUserInfo(
            userId = response.memberId.toString()
        )

        runCatching {
            fcmRemoteDataSource.sendToken(
                FcmRequest(
                    deviceToken = fcmToken,
                    deviceType = "ANDROID",
                    deviceId = deviceId
                )
            )
        }.onFailure { exception ->
            Log.e("AuthRepositoryImpl", "FCM 등록 실패: ${exception.message}")
            throw Exception("FCM 등록에 실패했습니다.", exception)
        }

        deviceDataStore.saveFcmToken(fcmToken)
    }

    override suspend fun verifyEmail(email: String) =
        authDataSource.verifyEmail(VerifyEmailRequest(email)).toDomain()

    override suspend fun refreshFcmToken(newToken: String): Result<Unit> = runCatching {
        deviceDataStore.saveFcmToken(newToken)
    }
}
