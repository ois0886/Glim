package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface BookService {

    @GET("api/v1/books")
    suspend fun getBooks(
        @Query ("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("searchQueryType") searchQueryType: String,
    ): List<BookResponse>

    @PATCH("api/v1/books/{id}/views")
    suspend fun updateViewCount(
        @Path("id") bookId: Long,
    )
}