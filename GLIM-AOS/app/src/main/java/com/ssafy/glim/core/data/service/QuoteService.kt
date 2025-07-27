package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.QuoteResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface QuoteService {

    @GET("api/v1/quotes")
    suspend fun getQuotes(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("sort") sort: String,
    ): List<QuoteResponse>

    @GET("api/v1/quotes/{isbn}")
    suspend fun getQuoteByIsbn(
        @Path("isbn") isbn: String
    ): List<QuoteResponse>

    @Multipart
    @POST("api/v1/quotes")
    suspend fun createQuote(
        @Part("quoteData") quoteData: RequestBody,
        @Part quoteImage: MultipartBody.Part?
    )

    @PATCH("api/v1/quotes/{id}/views")
    suspend fun updateQuoteViewCount(
        @Path("id") quoteId: Long,
    )
}
