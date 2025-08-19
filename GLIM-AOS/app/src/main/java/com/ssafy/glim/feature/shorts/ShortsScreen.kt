package com.ssafy.glim.feature.shorts

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.core.ui.DarkThemeScreen
import com.ssafy.glim.feature.main.excludeSystemBars
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun ShortsRoute(
    quoteId: Long,
    padding: PaddingValues,
    popBackStack: () -> Unit = {},
    viewModel: ShortsViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        Log.d("ShortsRoute", "ShortsRoute SideEffect triggered with quoteId: $quoteId")
        if (quoteId >= 0) {
            viewModel.loadQuote(quoteId)
        } else {
            viewModel.refresh()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is ShortsSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }

            is ShortsSideEffect.ShareQuote -> {
                Log.d("ShortsRoute", "Sharing quote URL: ${sideEffect.url}")
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, sideEffect.url.trim())
                }
                context.startActivity(Intent.createChooser(intent, "글귀 링크 공유"))
            }

            is ShortsSideEffect.ShareQuoteInstagram -> {
                val intent = Intent("com.instagram.share.ADD_TO_STORY").apply {
                    setDataAndType(sideEffect.imageUri, "image/*")
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    putExtra("source_application", context.packageName)
                }
                context.startActivity(intent)
            }

            is ShortsSideEffect.ShowMoreOptions -> {
                // 더보기 옵션 표시
                Toast.makeText(context, "준비 중인 기능입니다.", Toast.LENGTH_SHORT).show()
            }

            is ShortsSideEffect.CaptureSuccess -> {
                Toast.makeText(context, "이미지가 저장되었습니다: ${sideEffect.fileName}", Toast.LENGTH_SHORT)
                    .show()
            }

            is ShortsSideEffect.CaptureError -> {
                Toast.makeText(context, sideEffect.error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    val state by viewModel.collectAsState()

    val pagerState = rememberPagerState(pageCount = { state.quotes.size })

    LaunchedEffect(pagerState) {
        Log.d("ShortsRoute", "PagerState initialized with ${state.quotes.size} quotes")
        snapshotFlow { pagerState.currentPage }.collect { page ->
            viewModel.onPageChanged(page)
        }
    }

    DarkThemeScreen {
        VerticalPager(
            state = pagerState,
            modifier =
            Modifier
                .fillMaxSize()
                .padding(padding.excludeSystemBars())
        ) { page ->
            val quote = state.quotes[page]

            QuoteItem(
                quote = quote,
                modifier = Modifier.fillMaxSize(),
                onLikeClick = viewModel::toggleLike,
                onShareClick = viewModel::onShareClick,
                onInstagramShareClick = { viewModel.onInstagramShareClick(context) },
                onBookInfoClick = {
                    it?.let {
                        viewModel.onBookInfoClick(it)
                    }
                }
            )
        }
    }
}
