package com.ssafy.glim.core.data.network

import retrofit2.Response

fun <T, R> Response<T>.toResult(
    tag: String,
    successMessage: String = "Request successful",
    transform: (T) -> R
): Result<R> {
    return handleResponse(tag, successMessage, transform)
}