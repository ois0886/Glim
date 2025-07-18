package com.ssafy.glim.core.data.repository.fake

import android.util.Log
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.domain.model.Glim
import com.ssafy.glim.core.domain.model.RankStatus
import com.ssafy.glim.core.domain.model.SearchItem
import com.ssafy.glim.core.domain.repository.BookRepository
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class FakeBookRepositoryImpl
    @Inject
    constructor() : BookRepository {
        private val books = mutableListOf<Book>()

        init {
            books.add(
                Book(
                    title = "82년생 김지영",
                    author = "조남주",
                    publisher = "민음사",
                    publicationDate = "2016-10-14",
                    isbn = "9788937473142",
                    description = "한국 사회에서 여성으로 살아가는 것의 현실을 담담하게 그려낸 소설. 평범한 여성의 일상을 통해 우리 사회의 성차별 구조를 드러낸다.",
                    coverImageUrl = "https://example.com/book1.jpg"
                )
            )

            books.add(
                Book(
                    title = "미움받을 용기",
                    author = "기시미 이치로, 고가 후미타케",
                    publisher = "인플루엔셜",
                    publicationDate = "2014-11-17",
                    isbn = "9788966260720",
                    description = "아들러 심리학을 바탕으로 한 철학적 자기계발서. 타인의 시선에 얽매이지 않고 자신의 삶을 살아가는 방법을 제시한다.",
                    coverImageUrl = "https://example.com/book2.jpg"
                )
            )

            books.add(
                Book(
                    title = "채식주의자",
                    author = "한강",
                    publisher = "창비",
                    publicationDate = "2007-10-30",
                    isbn = "9788936434120",
                    description = "맨부커상 수상작. 한 여성의 채식 선언을 통해 억압적인 가부장제와 폭력성을 예리하게 그려낸 작품.",
                    coverImageUrl = "https://example.com/book3.jpg"
                )
            )

            books.add(
                Book(
                    title = "정의란 무엇인가",
                    author = "마이클 샌델",
                    publisher = "김영사",
                    publicationDate = "2010-05-25",
                    isbn = "9788934942467",
                    description = "하버드 대학교의 인기 철학 강의를 책으로 옮긴 작품. 정의에 대한 다양한 철학적 관점을 제시한다.",
                    coverImageUrl = "https://example.com/book4.jpg"
                )
            )

            books.add(
                Book(
                    title = "1984",
                    author = "조지 오웰",
                    publisher = "민음사",
                    publicationDate = "2003-08-25",
                    isbn = "9788937460777",
                    description = "전체주의 사회의 암울한 미래를 그린 디스토피아 소설의 고전. 빅 브라더와 감시 사회의 공포를 생생히 묘사한다.",
                    coverImageUrl = "https://example.com/book5.jpg"
                )
            )

            books.add(
                Book(
                    title = "사피엔스",
                    author = "유발 하라리",
                    publisher = "김영사",
                    publicationDate = "2015-11-02",
                    isbn = "9788934972464",
                    description = "호모 사피엔스가 어떻게 지구의 지배자가 되었는지를 과학적이고 철학적으로 분석한 인문학 베스트셀러.",
                    coverImageUrl = "https://example.com/book6.jpg"
                )
            )

            books.add(
                Book(
                    title = "완전한 행복",
                    author = "정유정",
                    publisher = "은행나무",
                    publicationDate = "2018-05-10",
                    isbn = "9791188810390",
                    description = "한국 추리소설의 새로운 지평을 연 작품. 완벽해 보이는 가족의 숨겨진 비밀을 파헤치는 심리 스릴러.",
                    coverImageUrl = "https://example.com/book7.jpg"
                )
            )

            books.add(
                Book(
                    title = "아몬드",
                    author = "손원평",
                    publisher = "창비",
                    publicationDate = "2017-03-31",
                    isbn = "9788936434434",
                    description = "감정을 느끼지 못하는 소년의 성장 이야기. 다름과 이해, 공감에 대한 따뜻한 메시지를 전한다.",
                    coverImageUrl = "https://example.com/book8.jpg"
                )
            )

            books.add(
                Book(
                    title = "해리 포터와 마법사의 돌",
                    author = "J.K. 롤링",
                    publisher = "문학수첩",
                    publicationDate = "1999-12-07",
                    isbn = "9788983920447",
                    description = "전 세계적으로 사랑받는 판타지 소설의 시작. 마법사가 된 해리 포터의 모험을 그린 첫 번째 이야기.",
                    coverImageUrl = "https://example.com/book9.jpg"
                )
            )

            books.add(
                Book(
                    title = "코스모스",
                    author = "칼 세이건",
                    publisher = "사이언스북스",
                    publicationDate = "2006-12-20",
                    isbn = "9788983711892",
                    description = "우주와 인간의 관계를 아름다운 문체로 그려낸 과학 교양서의 고전. 우주에 대한 경외감과 호기심을 자극한다.",
                    coverImageUrl = "https://example.com/book10.jpg"
                )
            )

            books.add(
                Book(
                    title = "작별하지 않는다",
                    author = "한강",
                    publisher = "문학동네",
                    publicationDate = "2021-09-03",
                    isbn = "9788954681841",
                    description = "제주 4·3사건을 소재로 한 소설. 역사의 상처와 기억, 그리고 희망에 대한 깊이 있는 성찰을 담았다.",
                    coverImageUrl = "https://example.com/book11.jpg"
                )
            )

            books.add(
                Book(
                    title = "데미안",
                    author = "헤르만 헤세",
                    publisher = "민음사",
                    publicationDate = "2000-03-15",
                    isbn = "9788937460579",
                    description = "한 소년의 성장과 자아 찾기를 그린 독일 문학의 고전. 선악과 자아 실현에 대한 철학적 성찰을 담았다.",
                    coverImageUrl = "https://example.com/book12.jpg"
                )
            )

            books.add(
                Book(
                    title = "왕좌의 게임",
                    author = "조지 R.R. 마틴",
                    publisher = "은행나무",
                    publicationDate = "2013-01-30",
                    isbn = "9791155554106",
                    description = "얼음과 불의 노래 시리즈 첫 번째 권. 권력을 둘러싼 치열한 투쟁과 복잡한 인간관계를 그린 대서사 판타지.",
                    coverImageUrl = "https://example.com/book13.jpg"
                )
            )

            books.add(
                Book(
                    title = "이방인",
                    author = "알베르 카뮈",
                    publisher = "민음사",
                    publicationDate = "2001-05-10",
                    isbn = "9788937460661",
                    description = "실존주의 문학의 대표작. 부조리한 현실 앞에서 무관심한 주인공을 통해 인간 존재의 의미를 탐구한다.",
                    coverImageUrl = "https://example.com/book14.jpg"
                )
            )

            books.add(
                Book(
                    title = "호밀밭의 파수꾼",
                    author = "J.D. 샐린저",
                    publisher = "민음사",
                    publicationDate = "2001-07-23",
                    isbn = "9788937460692",
                    description = "청소년 문학의 고전. 기성세대에 대한 반항과 순수성에 대한 갈망을 담은 성장 소설.",
                    coverImageUrl = "https://example.com/book15.jpg"
                )
            )
        }

    override fun searchBooks(query: String) = flow {
        emit(books.filter { book ->
            book.title.contains(query)
        })
    }
}
