package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.api.QuoteApi
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class QuoteRemoteDataSource @Inject constructor(
    private val quoteApi: QuoteApi
) {
    suspend fun getQuotes(
        page: Int,
        size: Int,
        sort: String
    ) = quoteApi.getQuotes(
        page = 0,
        size = size,
        sort = sort
    )

    suspend fun searchQuotes(
        content: String,
        page: Int,
        size: Int,
    ) = quoteApi.searchQuotes(content, page, size)

    suspend fun createQuote(
        quoteData: RequestBody,
        quoteImage: MultipartBody.Part?
    ) = quoteApi.createQuote(
        quoteData = quoteData,
        quoteImage = quoteImage
    )

    suspend fun updateQuoteViewCount(
        quoteId: Long
    ) = quoteApi.updateQuoteViewCount(quoteId)

    suspend fun getQuoteByIsbn(
        isbn: String
    ) = quoteApi.getQuoteByIsbn(isbn)

    suspend fun likeQuote(
        quoteId: Long
    ) = quoteApi.likeQuote(quoteId)

    suspend fun unLikeQuote(
        quoteId: Long
    ) = quoteApi.unLikeQuote(quoteId)

    suspend fun getQuoteById(
        quoteId: Long
    ) = quoteApi.getQuoteById(quoteId)

    suspend fun getMyUploadQuotes() = quoteApi.getMyUploadQuotes()

    suspend fun getMyLikedQuotes() = quoteApi.getMyLikedQuotes()
}
