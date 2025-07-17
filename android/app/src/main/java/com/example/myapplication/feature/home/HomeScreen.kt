package com.example.myapplication.feature.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.myapplication.R
import com.example.myapplication.feature.home.model.BookItem
import com.example.myapplication.feature.home.model.GlimInfo
import com.example.myapplication.feature.home.model.HomeSectionUiModel
import com.example.myapplication.ui.theme.GlimColor.LightGray300
import com.example.myapplication.ui.theme.GlimColor.LightGray600
import com.example.myapplication.ui.theme.GlimColor.LightPrimary300
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val homeUiState by viewModel.container.stateFlow.collectAsStateWithLifecycle()

    viewModel.collectSideEffect { effect ->
        when (effect) {
            is HomeSideEffect.ShowError ->
                Toast.makeText(context, effect.message, Toast.LENGTH_SHORT).show()
        }
    }

    HomeScreen(
        padding = padding,
        homeUiState = homeUiState,
        onGlimClick = viewModel::navigateToGlim,
        onBookClick = viewModel::navigateToBookDetail,
    )
}

@Composable
private fun HomeScreen(
    padding: PaddingValues,
    homeUiState: HomeUiState,
    onGlimClick: () -> Unit,
    onBookClick: (String) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)    ) {
        item {
            GlimHomeTitle(
                modifier = Modifier
            )
        }
        items(homeUiState.sections.size){ index ->
            val section = homeUiState.sections[index]
            when(section){
                is HomeSectionUiModel.GlimSection -> {
                    SectionTitle(section.title)
                    GlimCarousel(
                        glims = section.glims,
                        onItemClick = onGlimClick
                    )
                }
                is HomeSectionUiModel.BookSection -> {
                    SectionTitle(section.title)
                    BookCarousel(
                        books = section.books,
                        onItemClick = onBookClick
                    )
                }
            }
        }
    }
}

@Composable
fun GlimHomeTitle(modifier: Modifier) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp)) {
        Text(
            text = "오늘의 글림",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "잠시 멈춰 서서, 글귀를 둘러보세요.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 16.sp
            ),
            color = LightGray600
        )
    }
}
@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium.copy(
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        ),
        modifier = Modifier.padding(16.dp)
    )
}
@Composable
fun GlimCarousel(
    glims: List<GlimInfo>,
    onItemClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    itemSize: DpSize = DpSize(width = 280.dp, height = 160.dp),
    itemSpacing: Dp = 12.dp
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(glims.size) { index ->
            val glim = glims[index]
            Card(
                modifier = Modifier
                    .size(itemSize)
                    .clickable { onItemClick() },
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box {
                    Image(
                        painter = painterResource(R.drawable.example_glim_1),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "\"${glim.text}\"",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${glim.title} · ${glim.author}",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.White)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BookCarousel(
    books: List<BookItem>,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp),
    itemWidth: Dp = 100.dp,
    itemSpacing: Dp = 12.dp
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(books.size) { index ->
            val book = books[index]
            Column(
                modifier = Modifier
                    .width(itemWidth)
                    .clickable { onItemClick(book.id) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.size(itemWidth)
                ) {
                    Image(
                        painter = painterResource(R.drawable.example_glim_2),
                        contentDescription = book.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
