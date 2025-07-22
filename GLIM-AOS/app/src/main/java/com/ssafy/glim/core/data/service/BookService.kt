package com.ssafy.glim.core.data.service

import com.ssafy.glim.core.data.dto.response.BookResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface BookService {

    @GET
    suspend fun getBooks(
        @Query ("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("searchQueryType") searchQueryType: String,
    ): Response<List<BookResponse>>

    @PATCH
    suspend fun updateViewCount(
        @Query("id") bookId: Long,
    ): Response<Unit>
}