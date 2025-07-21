package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.GlimResponse
import com.ssafy.glim.core.data.service.QuoteService
import javax.inject.Inject

class QuoteRemoteDataSource @Inject constructor(
    private val service : QuoteService
) {
    suspend fun fetchQuotes(page: Int, size: Int, sort: String) : List<GlimResponse> {
        return service.getGlims(page, size, sort)
    }
}