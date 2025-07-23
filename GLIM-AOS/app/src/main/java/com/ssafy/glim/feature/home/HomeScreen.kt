package com.ssafy.glim.feature.home

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ssafy.glim.R
import com.ssafy.glim.feature.home.model.BookItem
import com.ssafy.glim.feature.home.model.GlimInfo
import com.ssafy.glim.feature.home.model.HomeSectionUiModel
import com.ssafy.glim.ui.theme.GlimColor.LightGray600
import com.ssafy.glim.ui.theme.GlimColor.LightGray700
import com.ssafy.glim.ui.theme.GlimColor.LightGray900
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
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        item {
            GlimHomeTitle()
        }
        items(homeUiState.sections) { section ->
            when (section) {
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
fun GlimHomeTitle() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(
            text = stringResource(R.string.today_glim),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.glim_home_description),
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
    itemSize: DpSize = DpSize(width = 240.dp, height = 360.dp),
    itemSpacing: Dp = 12.dp
) {
    LazyRow(
        modifier = modifier,
        contentPadding = contentPadding,
        horizontalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        items(glims) { glim ->
            Column(
                modifier = Modifier
                    .width(itemSize.width)
                    .clickable { onItemClick() },
                horizontalAlignment = Alignment.Start
            ) {
                Card(
                    modifier = Modifier.size(itemSize),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box {
                        Image(
                            painter = if(glim.background == "1") painterResource(R.drawable.example_glim_3) else if(glim.background == "2") painterResource(R.drawable.example_glim_4) else painterResource(R.drawable.example_glim_1),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(itemSize)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = glim.title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = LightGray900
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = glim.author,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 14.sp,
                        color = LightGray700
                    ),
                )
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
        items(books) { book ->
            Column(
                modifier = Modifier
                    .width(itemWidth)
                    .clickable { onItemClick(book.id) },
                horizontalAlignment = Alignment.Start
            ) {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.size(itemWidth)
                ) {
                    Image(
                        painter = if(book.bookCover == "1") painterResource(R.drawable.example_glim_3) else if(book.bookCover == "2") painterResource(R.drawable.example_glim_4) else painterResource(R.drawable.example_glim_2),
                        contentDescription = book.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
