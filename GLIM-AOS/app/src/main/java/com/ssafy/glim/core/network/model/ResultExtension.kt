package com.ssafy.glim.core.network.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Retrofit Response를 Result로 변환하는 확장 함수
 */
fun <T> Response<T>.toResult(): Result<T> {
    return if (isSuccessful) {
        body()?.let { Result.success(it) }
            ?: Result.failure(Exception("Response body is null"))
    } else {
        val errorMessage =
            when (code()) {
                400 -> "잘못된 요청입니다."
                401 -> "인증이 필요합니다."
                403 -> "접근 권한이 없습니다."
                404 -> "요청한 리소스를 찾을 수 없습니다."
                409 -> "이미 존재하는 데이터입니다."
                422 -> "입력 데이터를 확인해주세요."
                500 -> "서버 오류가 발생했습니다."
                else -> "요청에 실패했습니다. (${code()})"
            }
        Result.failure(Exception(errorMessage))
    }
}

suspend inline fun <T> safeApiCall(crossinline apiCall: suspend () -> T): Result<T> {
    return runCatching { apiCall() }
        .fold(
            onSuccess = { Result.success(it) },
            onFailure = { exception ->
                val errorMessage =
                    when (exception) {
                        is UnknownHostException -> "네트워크 연결을 확인해주세요."
                        is SocketTimeoutException -> "요청 시간이 초과되었습니다."
                        is IOException -> "네트워크 오류가 발생했습니다."
                        is HttpException ->
                            when (exception.code()) {
                                400 -> "잘못된 요청입니다."
                                401 -> "인증이 필요합니다."
                                403 -> "접근 권한이 없습니다."
                                404 -> "요청한 리소스를 찾을 수 없습니다."
                                409 -> "이미 존재하는 데이터입니다."
                                422 -> "입력 데이터를 확인해주세요."
                                500 -> "서버 오류가 발생했습니다."
                                else -> "요청에 실패했습니다."
                            }

                        else -> exception.message ?: "알 수 없는 오류가 발생했습니다."
                    }
                Result.failure(Exception(errorMessage))
            },
        )
}

/**
 * Flow에서 Result를 안전하게 처리하는 확장 함수
 */
inline fun <T> flowOfResult(crossinline block: suspend () -> Result<T>): Flow<Result<T>> =
    flow {
        emit(block())
    }
