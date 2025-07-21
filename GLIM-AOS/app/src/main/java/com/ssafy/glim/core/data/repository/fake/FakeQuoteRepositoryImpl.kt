package com.ssafy.glim.core.data.repository.fake

import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.domain.repository.BookRepository
import com.ssafy.glim.core.domain.repository.QuoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeQuoteRepositoryImpl
    @Inject
    constructor() : QuoteRepository {
        private val quotes = mutableListOf<Quote>()

        init {
// 82년생 김지영
            quotes.add(
                Quote(
                    text = "나는 그저 평범하게 살고 싶었을 뿐이다.",
                    bookTitle = "82년생 김지영",
                    page = "P.156",
                    likes = 2847
                )
            )

            quotes.add(
                Quote(
                    text = "여자는 누구나 김지영이다.",
                    bookTitle = "82년생 김지영",
                    page = "P.201",
                    likes = 1923
                )
            )

// 미움받을 용기
            quotes.add(
                Quote(
                    text = "타인의 과제에 개입하지 말고, 자신의 과제에 타인을 개입시키지 마라.",
                    bookTitle = "미움받을 용기",
                    page = "P.142",
                    likes = 3456
                )
            )

            quotes.add(
                Quote(
                    text = "중요한 것은 주어진 것이 아니라 그것을 어떻게 활용하느냐이다.",
                    bookTitle = "미움받을 용기",
                    page = "P.89",
                    likes = 2134
                )
            )

// 채식주의자
            quotes.add(
                Quote(
                    text = "이젠 더이상 두 글자도 않아. 왜지. 왜 나는 이렇게 말라가는 거지.",
                    bookTitle = "채식주의자",
                    page = "P.51",
                    likes = 1247
                )
            )

            quotes.add(
                Quote(
                    text = "꿈이 너무 생생해서 깨어나고 싶지 않았다.",
                    bookTitle = "채식주의자",
                    page = "P.94",
                    likes = 987
                )
            )

// 아몬드
            quotes.add(
                Quote(
                    text = "감정이 없는 것과 표현하지 않는 것은 다르다.",
                    bookTitle = "아몬드",
                    page = "P.178",
                    likes = 2567
                )
            )

            quotes.add(
                Quote(
                    text = "우리는 모두 다르고, 그 다름이 틀림이 아니다.",
                    bookTitle = "아몬드",
                    page = "P.205",
                    likes = 3278
                )
            )

// 1984
            quotes.add(
                Quote(
                    text = "빅 브라더가 당신을 지켜보고 있다.",
                    bookTitle = "1984",
                    page = "P.3",
                    likes = 4123
                )
            )

            quotes.add(
                Quote(
                    text = "자유는 2+2=4라고 말할 수 있는 자유이다.",
                    bookTitle = "1984",
                    page = "P.69",
                    likes = 2891
                )
            )

// 사피엔스
            quotes.add(
                Quote(
                    text = "역사란 우연의 연속이다.",
                    bookTitle = "사피엔스",
                    page = "P.267",
                    likes = 1789
                )
            )

            quotes.add(
                Quote(
                    text = "인간은 이야기를 믿는 유일한 동물이다.",
                    bookTitle = "사피엔스",
                    page = "P.31",
                    likes = 2456
                )
            )

// 데미안
            quotes.add(
                Quote(
                    text = "새는 알을 깨고 나온다. 알은 세계이다.",
                    bookTitle = "데미안",
                    page = "P.112",
                    likes = 3789
                )
            )

            quotes.add(
                Quote(
                    text = "각자의 인생은 자기 자신에게로 이르는 길이다.",
                    bookTitle = "데미안",
                    page = "P.158",
                    likes = 2345
                )
            )

// 이방인
            quotes.add(
                Quote(
                    text = "오늘 엄마가 죽었다. 아니 어제였을지도 모른다.",
                    bookTitle = "이방인",
                    page = "P.7",
                    likes = 1567
                )
            )

            quotes.add(
                Quote(
                    text = "나는 행복했고, 지금도 행복하다.",
                    bookTitle = "이방인",
                    page = "P.123",
                    likes = 2234
                )
            )

// 호밀밭의 파수꾼
            quotes.add(
                Quote(
                    text = "어른들은 모두 가짜다.",
                    bookTitle = "호밀밭의 파수꾼",
                    page = "P.89",
                    likes = 1678
                )
            )

            quotes.add(
                Quote(
                    text = "나는 호밀밭의 파수꾼이 되고 싶다.",
                    bookTitle = "호밀밭의 파수꾼",
                    page = "P.173",
                    likes = 2890
                )
            )

// 정의란 무엇인가
            quotes.add(
                Quote(
                    text = "정의란 각자에게 그의 몫을 주는 것이다.",
                    bookTitle = "정의란 무엇인가",
                    page = "P.45",
                    likes = 1456
                )
            )

            quotes.add(
                Quote(
                    text = "옳은 일을 하는 것과 좋은 일을 하는 것은 다르다.",
                    bookTitle = "정의란 무엇인가",
                    page = "P.167",
                    likes = 2123
                )
            )

// 완전한 행복
            quotes.add(
                Quote(
                    text = "완전한 행복은 존재하지 않는다. 그것은 추구하는 것이다.",
                    bookTitle = "완전한 행복",
                    page = "P.234",
                    likes = 1345
                )
            )

            quotes.add(
                Quote(
                    text = "사랑은 때로 가장 완벽한 범죄가 된다.",
                    bookTitle = "완전한 행복",
                    page = "P.189",
                    likes = 987
                )
            )

// 작별하지 않는다
            quotes.add(
                Quote(
                    text = "기억하는 것이 곧 존재하는 것이다.",
                    bookTitle = "작별하지 않는다",
                    page = "P.145",
                    likes = 2567
                )
            )

            quotes.add(
                Quote(
                    text = "우리는 작별하지 않는다. 단지 만나지 않을 뿐이다.",
                    bookTitle = "작별하지 않는다",
                    page = "P.278",
                    likes = 3456
                )
            )

// 코스모스
            quotes.add(
                Quote(
                    text = "우리는 모두 별의 재료로 만들어졌다.",
                    bookTitle = "코스모스",
                    page = "P.189",
                    likes = 4567
                )
            )

            quotes.add(
                Quote(
                    text = "우주는 우리가 아는 것보다 더 신비롭다.",
                    bookTitle = "코스모스",
                    page = "P.56",
                    likes = 2789
                )
            )

// 해리 포터와 마법사의 돌
            quotes.add(
                Quote(
                    text = "행복은 찾을 수 있는 가장 어두운 곳에서도 발견할 수 있다.",
                    bookTitle = "해리 포터와 마법사의 돌",
                    page = "P.237",
                    likes = 5678
                )
            )

            quotes.add(
                Quote(
                    text = "우리의 선택이 우리가 누구인지 보여준다.",
                    bookTitle = "해리 포터와 마법사의 돌",
                    page = "P.298",
                    likes = 4234
                )
            )

// 왕좌의 게임
            quotes.add(
                Quote(
                    text = "겨울이 오고 있다.",
                    bookTitle = "왕좌의 게임",
                    page = "P.45",
                    likes = 3789
                )
            )

            quotes.add(
                Quote(
                    text = "죽음이 모든 사람에게 평등하다면, 삶은 그렇지 않다.",
                    bookTitle = "왕좌의 게임",
                    page = "P.567",
                    likes = 2456
                )
            )
        }

    override fun searchQuotes(query: String) = flow {
        emit(quotes.filter { quote ->
            quote.text.contains(query)
        })
    }

    override fun getQuotes(userId: String): Flow<List<Quote>> {
        TODO("Not yet implemented")
    }
}
