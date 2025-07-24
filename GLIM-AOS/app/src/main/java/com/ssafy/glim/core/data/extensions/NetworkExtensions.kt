package com.ssafy.glim.core.data.extensions

import android.util.Log
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Response

fun Any.toJsonRequestBody(): RequestBody {
    val gson = Gson()
    return gson.toJson(this).toRequestBody("application/json".toMediaTypeOrNull())
}

private fun <T, R> Response<T>.handleResponse(
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

fun <T, R> Response<T>.toResult(
    tag: String,
    successMessage: String = "Request successful",
    transform: (T) -> R
): Result<R> {
    return handleResponse(tag, successMessage, transform)
}
