package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.GlimResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class QuoteRemoteDataSource
    @Inject
    constructor() {
        private val all =
            List(50) { idx ->
                GlimResponse(
                    quoteId = idx.toLong(),
                    quoteImageName = "https://picsum.photos/seed/glim_$idx/800/1400",
                    bookTitle = "Quote Title #$idx",
                    author = "Author $idx",
                    bookId = 0,
                    bookCoverUrl = "",
                    quoteViews = 0,
                )
            }

        fun fetchQuotes(
            page: Int,
            size: Int,
            sort: String,
        ): Flow<List<GlimResponse>> =
            flow {
                val from = page * size
                val to = (from + size).coerceAtMost(all.size)
                val slice = if (from < to) all.subList(from, to) else emptyList()
                emit(slice)
            }
    }
