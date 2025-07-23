package com.ssafy.glim.core.data.network

import android.util.Log
import retrofit2.Response

fun <T, R> Response<T>.handleResponse(
    tag: String,
    successMessage: String,
    transform: (T) -> R
): Result<R> {
    return try {
        if (isSuccessful) {
            val body = body() ?: throw Exception("Response body is null")
            Log.d(tag, successMessage)
            Result.success(transform(body))
        } else {
            throw Exception("Request failed with code: ${code()} message : ${errorBody()?.string()}")
        }
    } catch (e: Exception) {
        Log.e(tag, "${e.message}")
        Result.failure(e)
    }
}

