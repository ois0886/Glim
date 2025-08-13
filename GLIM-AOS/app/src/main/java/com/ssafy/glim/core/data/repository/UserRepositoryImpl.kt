package com.ssafy.glim.core.data.repository

import android.graphics.Bitmap
import android.util.Log
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.authmanager.LogoutReason
import com.ssafy.glim.core.data.datasource.remote.UserRemoteDataSource
import com.ssafy.glim.core.data.datastore.DeviceDataStore
import com.ssafy.glim.core.data.dto.request.DeleteUserRequest
import com.ssafy.glim.core.data.dto.request.LogOutRequest
import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.extensions.toImagePart
import com.ssafy.glim.core.data.extensions.toJsonRequestBody
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val authDataSource: UserRemoteDataSource,
    private val authManager: AuthManager,
    private val deviceDataStore: DeviceDataStore
) : UserRepository {

    override suspend fun getUserById(): User {
        val id = requireNotNull(authManager.getUserId()) {
            "사용자 ID를 찾을 수 없습니다."
        }
        return authDataSource.getUserById(id).toDomain()
    }

    override suspend fun updateUser(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: List<Int>,
        profileImage: Bitmap
    ): User {
        val request = UpdateUserRequest(
            password = password,
            nickname = nickname,
            gender = gender,
            birthDate = birthDate,
        )

        val jsonRequestBody = request.toJsonRequestBody()

        val profileImagePart = profileImage.toImagePart(
            partName = "profileImage",
            fileName = "profile_${System.currentTimeMillis()}.jpg",
            quality = 85
        ) ?: throw IllegalStateException("프로필 이미지 변환에 실패했습니다")

        return authDataSource.updateUser(memberId, jsonRequestBody, profileImagePart).toDomain()
    }

    override suspend fun logout() {
        val deviceId = deviceDataStore.getDeviceId()

        runCatching {
            authDataSource.logout(LogOutRequest(deviceId))
        }.onSuccess {
            deviceDataStore.clearFcmToken()
            authManager.logout(LogoutReason.UserLogout)

        }.onFailure { error ->
            Log.e("AuthRepositoryImpl", "로그아웃 실패: ${error.message}")
            throw error
        }
    }

    override suspend fun deleteUser() {
        val deviceId = deviceDataStore.getDeviceId()

        runCatching {
            authDataSource.deleteUser(request = DeleteUserRequest(deviceId)).toDomain()
        }.onSuccess {
            deviceDataStore.clearFcmToken()
            authManager.logout(LogoutReason.UserWithDrawl)
        }.onFailure { error ->
            Log.e("UserRepositoryImpl", "회원탈퇴 실패: ${error.message}")
            throw Exception("회원탈퇴에 실패했습니다.", error)
        }
    }
}
