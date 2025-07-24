package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.GlimResponse
import com.ssafy.glim.core.data.service.QuoteService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuoteRemoteDataSource
@Inject
constructor(
    private val service: QuoteService
) {

    fun fetchQuotes(
        page: Int,
        size: Int,
        sort: String,
    ): Flow<List<GlimResponse>> =
        flow {
            emit(service.getGlims(page, size, sort))
        }
}
