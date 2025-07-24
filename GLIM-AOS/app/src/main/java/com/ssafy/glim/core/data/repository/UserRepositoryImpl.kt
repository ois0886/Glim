package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.common.extensions.toBirthDateList
import com.ssafy.glim.core.data.datasource.remote.UserRemoteDataSource
import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.mapper.toDomain
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.repository.UserRepository
import jakarta.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val dataSource: UserRemoteDataSource,
) : UserRepository {

    override suspend fun getUserByEmail(email: String) =
        dataSource.getUserByEmail(email).toDomain()

    override suspend fun updateUser(
        memberId: Long,
        password: String,
        nickname: String,
        gender: String,
        birthDate: String,
    ): User {
        val birthDateList = birthDate.toBirthDateList()
        val request = UpdateUserRequest(
            password = password,
            nickname = nickname,
            gender = gender,
            birthDate = birthDateList,
        )
        return dataSource.updateUser(memberId, request).toDomain()
    }

    override suspend fun deleteUser(memberId: Long) =
        dataSource.deleteUser(memberId).toDomain()
}
