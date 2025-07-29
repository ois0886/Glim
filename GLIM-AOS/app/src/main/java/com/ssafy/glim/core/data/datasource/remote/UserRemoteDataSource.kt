package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.service.UserService
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val service: UserService,
) {

    suspend fun getUserById(memberId: String) = service.getUserById(memberId)

    suspend fun updateUser(memberId: Long, request: UpdateUserRequest) =
        service.updateUser(memberId, request)

    suspend fun deleteUser(memberId: Long) = service.deleteUser(memberId)
}
