package com.ssafy.glim.core.data.repository.fake

import com.ssafy.glim.core.domain.model.RankStatus
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.repository.SearchQueryRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class
FakeSearchQueryRepositoryImpl@Inject
constructor() : SearchQueryRepository {
    private val popularSearchQueries =
        listOf(
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 1,
                text = "삼성전자",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.DOWN,
                rank = 2,
                text = "SK하이닉스",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.MAINTAIN,
                rank = 3,
                text = "LG화학",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 4,
                text = "카카오",
                type = "IT",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 5,
                text = "네이버",
                type = "IT",
            ),
            SearchItem(
                rankStatus = RankStatus.DOWN,
                rank = 6,
                text = "현대차",
                type = "자동차",
            ),
            SearchItem(
                rankStatus = RankStatus.MAINTAIN,
                rank = 7,
                text = "기아",
                type = "자동차",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 8,
                text = "셀트리온",
                type = "바이오",
            ),
            SearchItem(
                rankStatus = RankStatus.DOWN,
                rank = 9,
                text = "POSCO홀딩스",
                type = "철강",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 10,
                text = "LG에너지솔루션",
                type = "배터리",
            ),
        )

    private val recentSearchQueries =
        mutableListOf(
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 1,
                text = "삼성전자",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.DOWN,
                rank = 2,
                text = "SK하이닉스",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.MAINTAIN,
                rank = 3,
                text = "LG화학",
                type = "주식",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 4,
                text = "카카오",
                type = "IT",
            ),
            SearchItem(
                rankStatus = RankStatus.UP,
                rank = 5,
                text = "네이버",
                type = "IT",
            ),
        )

    override fun getPopularSearchQueries() =
        flow {
            emit(popularSearchQueries)
        }

    override fun getRecentSearchQueries() =
        flow {
            emit(recentSearchQueries)
        }

    override fun saveRecentSearchQuery(query: String) =
        flow {
            recentSearchQueries.removeIf { it.text == query }
            recentSearchQueries.add(0, SearchItem(text = query))
            emit(Unit)
        }

    override fun deleteRecentSearchQuery(query: String) =
        flow {
            recentSearchQueries.removeIf { it.text == query }
            emit(Unit)
        }
}
