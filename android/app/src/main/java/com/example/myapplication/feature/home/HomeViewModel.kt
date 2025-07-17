package com.example.myapplication.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.core.navigation.Navigator
import com.example.myapplication.feature.home.model.BookItem
import com.example.myapplication.feature.home.model.GlimInfo
import com.example.myapplication.feature.home.model.HomeSectionUiModel
import com.example.myapplication.feature.main.MainTab
import com.example.myapplication.feature.reels.GlimItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel(), ContainerHost<HomeUiState, HomeSideEffect> {
    override val container = container<HomeUiState, HomeSideEffect>(initialState = HomeUiState())

    private val _errorFlow = MutableSharedFlow<Throwable>()
    val errorFlow get() = _errorFlow.asSharedFlow()

    fun navigateToGlim() = viewModelScope.launch {
        navigator.navigate(MainTab.REELS.route)
    }

    fun navigateToBookDetail(bookId: String) = viewModelScope.launch {
        // 책 상세 정보로 navigate :  navigator.navigate()
    }

    init {
        loadDummyData()
    }

    private fun loadDummyData() = intent {
        // 1) 로딩 시작
        reduce { state.copy(isLoading = true) }

        // 2) (실제론 서버 호출) → 여기선 더미 딜레이
        delay(300)

        // 3) 더미 데이터 생성
        val todayQuotes = listOf(
            GlimInfo("q1", "이젠 더이상 둥글지도 않아…", "채식주의자", "한강"),
            GlimInfo("q2", "어떤 기억은 시간이 흐른다고 사라지는 것이 아니라…", "소년이 온다", "한강")
        )
        val hangangQuotes = listOf(
            GlimInfo("q3", "그녀는 바람이었다…", "작별하지 않는다", "한강")
        )
        val bestSellers = listOf(
            BookItem("b1", "회람의 시간"),
            BookItem("b2", "작별하지 않는다"),
            BookItem("b3", "소년이 온다")
        )
        val rainyDayBooks = listOf(
            BookItem("b4", "바람이 분다, 가라"),
            BookItem("b5", "흰")
        )
        val sections = listOf(
            HomeSectionUiModel.GlimSection("today_quote", "오늘의 추천 글귀", todayQuotes),
            HomeSectionUiModel.GlimSection("hangang_collection", "한강 작가 컬렉션", hangangQuotes),
            HomeSectionUiModel.BookSection("best_seller", "베스트셀러", bestSellers),
            HomeSectionUiModel.BookSection("rainy_day", "비오는 날 보기 좋은 책", rainyDayBooks)
        )

        // 4) 로딩 끝, 섹션 채우기
        reduce {
            state.copy(
                isLoading = false,
                sections = sections
            )
        }
    }
}