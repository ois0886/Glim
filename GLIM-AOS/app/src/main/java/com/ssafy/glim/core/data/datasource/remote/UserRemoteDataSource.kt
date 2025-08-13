package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.api.UserApi
import com.ssafy.glim.core.data.dto.request.DeleteUserRequest
import com.ssafy.glim.core.data.dto.request.LogOutRequest
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val service: UserApi,
) {

    suspend fun getUserById(memberId: String) = service.getUserById(memberId)

    suspend fun updateUser(memberId: Long, request: UpdateUserRequest) =
        service.updateUser(memberId, request)

    suspend fun deleteUser(request: DeleteUserRequest) = service.deleteUser(request)

    suspend fun logout(request: LogOutRequest) = service.logout(request)
}
