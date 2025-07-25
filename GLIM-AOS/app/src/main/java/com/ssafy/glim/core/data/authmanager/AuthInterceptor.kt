package com.ssafy.glim.core.data.authmanager

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val authManager: AuthManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJob25nQG5hdmVyLmNvbSIsImlhdCI6MTc1MzQ1MDI2NiwiZXhwIjoxNzUzNDUzODY2LCJtZW1iZXJJZCI6MiwidHlwZSI6ImFjY2VzcyIsImp0aSI6IjE0NmJlMjRhLTg0YWYtNGJlMy1hMTM3LTBhMTQ0MWRmMTM4OSJ9.4VPp77DIi-FCHsKJwClppofW54pXgxB1pNOTg6v8SBE"

        if (token == null) {
            Log.d("AuthInterceptor", "Token is null. Authorization header not added.")
        }

        val request = chain.request().newBuilder()
            .apply {
                if (!token.isNullOrEmpty()) {
                    Log.d("AuthInterceptor", token)
                    addHeader("Authorization", "Bearer $token")
                }
            }
            .build()
        return chain.proceed(request)
    }
}
