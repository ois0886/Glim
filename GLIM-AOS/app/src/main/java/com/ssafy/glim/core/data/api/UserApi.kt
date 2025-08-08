package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.dto.response.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserApi {

    // 사용자 정보를 이메일로 조회
    @GET("api/v1/members/{memberId}")
    suspend fun getUserById(
        @Path("memberId") memberId: String,
    ): UserResponse

    // 사용자 정보 수정
    @PUT("api/v1/members/{memberId}")
    suspend fun updateUser(
        @Path("memberId") memberId: Long,
        @Body request: UpdateUserRequest,
    ): UserResponse

    // 사용자 정보 삭제 (회원 탈퇴)
    @PATCH("api/v1/members/{memberId}/status")
    suspend fun deleteUser(
        @Path("memberId") memberId: Long,
    ): UserResponse
}
