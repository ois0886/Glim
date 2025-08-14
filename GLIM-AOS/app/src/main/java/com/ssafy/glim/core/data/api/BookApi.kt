package com.ssafy.glim.core.data.api

import com.ssafy.glim.core.data.dto.response.BookDetailResponse
import com.ssafy.glim.core.data.dto.response.BookResponse
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface BookApi {

    @GET("api/v1/books")
    suspend fun getBooks(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("searchQueryType") searchQueryType: String,
    ): List<BookResponse>

    @GET("api/v1/books/{id}")
    suspend fun getBook(
        @Path("id") bookId: Long,
    ): BookDetailResponse

    @PATCH("api/v1/books/{id}/views")
    suspend fun updateViewCount(
        @Path("id") bookId: Long,
    )
}
