package com.ssafy.glim.feature.celebrations

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GifDisplay
import com.ssafy.glim.ui.theme.MyApplicationTheme
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun CelebrationsRoute(
    padding: PaddingValues,
    nickname: String,
    viewModel: CelebrationsViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is CelebrationsSideEffect.NavigateToLogin -> {
                viewModel.navigateToHome()
            }
        }
    }

    LaunchedEffect(nickname) {
        viewModel.startCelebration(nickname)
    }

    CelebrationsScreen(
        state = state,
        padding = padding
    )
}

@Composable
private fun CelebrationsScreen(
    state: CelebrationsUiState,
    padding: PaddingValues = PaddingValues(0.dp)
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp)
    ) {
        // 왼쪽 celebration
        GifDisplay(
            gifResourceId = R.drawable.celebration,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .size(140.dp)
        )

        // 오른쪽 celebration
        GifDisplay(
            gifResourceId = R.drawable.celebration,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(140.dp)
        )

        // 가운데 콘텐츠
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(id = R.string.welcome_message, state.nickname),
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            GifDisplay(
                gifResourceId = R.drawable.book,
                modifier = Modifier.size(120.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CelebrationsScreenPreview() {
    MyApplicationTheme {
        CelebrationsScreen(
            state = CelebrationsUiState(nickname = "Test")
        )
    }
}

@Preview(showBackground = true, name = "긴 닉네임")
@Composable
fun CelebrationsScreenLongNicknamePreview() {
    MyApplicationTheme {
        CelebrationsScreen(
            state = CelebrationsUiState(nickname = "TestTest")
        )
    }
}

@Preview(showBackground = true, name = "다크 테마", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CelebrationsScreenDarkPreview() {
    MyApplicationTheme {
        CelebrationsScreen(
            state = CelebrationsUiState(nickname = "TestTestTest")
        )
    }
}
