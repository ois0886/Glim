package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.UserApi
import com.ssafy.glim.core.data.dto.request.DeleteUserRequest
import com.ssafy.glim.core.data.dto.request.LogOutRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class UserRemoteDataSource @Inject constructor(
    private val service: UserApi,
) {

    suspend fun getUserById(memberId: String) = service.getUserById(memberId)

    suspend fun updateUser(memberId: Long, request: RequestBody, profileImage: MultipartBody.Part?) =
        service.updateUser(memberId, request, profileImage)

    suspend fun deleteUser(request: DeleteUserRequest) = service.deleteUser(request)

    suspend fun logout(request: LogOutRequest) = service.logout(request)
}
