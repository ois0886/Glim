package com.ssafy.glim.core.data.api

import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

import com.google.gson.annotations.SerializedName

data class ShortenUrlRequest(
    @SerializedName("long_url")
    val longUrl: String,
    @SerializedName("domain")
    val domain: String = "bit.ly"
)

data class ShortenUrlResponse(
    @SerializedName("link")
    val link: String,
    @SerializedName("long_url")
    val longUrl: String,
    @SerializedName("id")
    val id: String
)

interface ShortLinkApi {

    @POST("v4/shorten")
    suspend fun shortenUrl(
        @Header("Authorization") authorization: String,
        @Header("Content-Type") contentType: String = "application/json",
        @Body request: ShortenUrlRequest
    ): ShortenUrlResponse
}
