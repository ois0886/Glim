package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.GlimResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface QuoteService {

    @GET("quotes")
    suspend fun getGlims(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String,
    ): List<GlimResponse>

    @Multipart
    @POST("api/v1/quotes")
    suspend fun createQuote(
        @Part("quoteData") quoteData: RequestBody,
        @Part quoteImage: MultipartBody.Part?
    ): Response<Unit>
}