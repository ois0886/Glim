package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.request.DeleteUserRequest
import com.ssafy.glim.core.data.dto.request.LogOutRequest
import com.ssafy.glim.core.data.dto.request.UpdateUserRequest
import com.ssafy.glim.core.data.dto.response.UserResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface UserApi {

    // 사용자 정보를 아이디로 조회
    @GET("api/v1/members/{memberId}")
    suspend fun getUserById(
        @Path("memberId") memberId: String,
    ): UserResponse

    // 사용자 정보 수정
    @Multipart
    @PUT("api/v1/members/{memberId}")
    suspend fun updateUser(
        @Path("memberId") memberId: Long,
        @Part request: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): UserResponse

    // 사용자 정보 삭제 (회원 탈퇴)
    @PATCH("api/v1/members/me/status")
    suspend fun deleteUser(
        @Body request: DeleteUserRequest
    ): UserResponse

    // 로그아웃
    @POST("/api/v1/auth/logout")
    suspend fun logout(
        @Body request: LogOutRequest
    )
}
