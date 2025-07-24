package com.ssafy.glim.core.data.datasource.remote

import com.ssafy.glim.core.data.dto.response.CurationContentResponse
import com.ssafy.glim.core.data.dto.response.CurationItemResponse
import javax.inject.Inject

class TempCurationRemoteDataSource @Inject constructor() {
    fun fetchMainCurations(): List<CurationItemResponse> = listOf(
        // 1) 인기 글귀
        CurationItemResponse(
            curationItemId = null,
            title = "현재 인기 많은 글귀",
            description = "현재 인기 많은 글귀 입니다",
            curationType = "QUOTE",
            contents = listOf(
                CurationContentResponse(
                    bookId = 2L,
                    bookTitle = "채식주의자",
                    author = "작가",
                    publisher = "출판사",
                    bookCoverUrl = null,
                    quoteId = 1L,
                    imageName = "image.jpg"
                )
            )
        ),
        // 2) 인기 도서
        CurationItemResponse(
            curationItemId = null,
            title = "현재 인기 많은 도서",
            description = "현재 인기 많은 도서 입니다",
            curationType = "BOOK",
            contents = listOf(
                CurationContentResponse(
                    bookId = null,
                    bookTitle = "소년이 온다 - 2024 노벨문학상 수상작가",
                    author = "한강 (지은이)",
                    publisher = "창비",
                    bookCoverUrl = "https://image.aladin.co.kr/product/4086/97/coversum/8936434128_2.jpg",
                    quoteId = null,
                    imageName = null
                ),
                CurationContentResponse(
                    bookId = null,
                    bookTitle = "2025 큰별쌤 최태성의 별★별한국사 한국사능력검정시험 심화(1, 2, 3급) 상",
                    author = "최태성 (지은이)",
                    publisher = "이투스북",
                    bookCoverUrl = "https://image.aladin.co.kr/product/35398/17/coversum/k432035019_1.jpg",
                    quoteId = null,
                    imageName = null
                )
            )
        )
    )
}
