package com.ssafy.glim.core.data.repository

import android.util.Log
import com.ssafy.glim.core.data.authmanager.AuthManager
import com.ssafy.glim.core.data.datasource.remote.UserRemoteDataSource
import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.repository.UserRepository
import com.ssafy.glim.core.data.authmanager.LogoutReason
import jakarta.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserRemoteDataSource,
    private val authManager: AuthManager
) : UserRepository {

    override suspend fun getUserById(): User {
        val id = requireNotNull(authManager.getUserId()) {
            "사용자 ID를 찾을 수 없습니다."
        }
        return dataSource.getUserById(id).toDomain()
    }

    override suspend fun updateUser(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: List<Int>,
    ): User {
        val request = UpdateUserRequest(
            password = password,
            nickname = nickname,
            gender = gender,
            birthDate = birthDate,
        )
        return dataSource.updateUser(memberId, request).toDomain()
    }

    override suspend fun deleteUser() {
        val id = requireNotNull(authManager.getUserId()) {
            "사용자 ID를 찾을 수 없습니다."
        }
        runCatching { dataSource.deleteUser(id.toLong()).toDomain() }
            .onSuccess { authManager.logout(LogoutReason.UserWithDrawl) }
            .onFailure { Log.d("UserRepositoryImpl", "deleteUser failed: ${it.message}") }
    }
}
