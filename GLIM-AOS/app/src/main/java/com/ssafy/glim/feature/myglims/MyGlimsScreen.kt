package com.ssafy.glim.feature.myglims

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun MyGlimsRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    listType: MyGlimsType = MyGlimsType.LIKED,
    viewModel: MyGlimsViewModel = hiltViewModel()
) {
    viewModel.collectSideEffect { effect ->
        when (effect) {
            is MyGlimsSideEffect.ShowToast ->
                // TODO: Toast(effect.message)
                Unit
        }
    }

    val uiState by viewModel.collectAsState()

    LaunchedEffect(listType) {
        viewModel.loadMyGlims(listType)
    }

    MyGlimsScreen(
        uiState = uiState,
        listType = listType,
        onBackClick = popBackStack,
        onLikeClick = { glimId -> viewModel.toggleLike(glimId) },
        onTabChange = { newType -> viewModel.loadMyGlims(newType) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyGlimsScreen(
    uiState: MyGlimsUiState,
    listType: MyGlimsType,
    onBackClick: () -> Unit,
    onLikeClick: (Long) -> Unit,
    onTabChange: (MyGlimsType) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 상단 앱바
        TopAppBar(
            title = {
                Text(
                    text = listType.displayName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "뒤로가기",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // 로딩 상태 처리
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // 글귀 리스트
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(
                    count = uiState.myGlims.size,
                    key = { index -> uiState.myGlims[index].id }
                ) { index ->
                    val glim = uiState.myGlims[index]
                    MyGlimsItem(
                        glim = glim,
                        onLikeClick = { onLikeClick(glim.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyGlimsItem(
    glim: GlimItem,
    onLikeClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* 상세 화면으로 이동 */ },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = glim.content,
                fontSize = 16.sp,
                color = Color.Black,
                lineHeight = 24.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(
                                Color.Blue,
                                RoundedCornerShape(4.dp)
                            )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = glim.author,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

                // 좋아요 버튼
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onLikeClick,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = if (glim.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "좋아요",
                            tint = if (glim.isLiked) Color.Red else Color.Gray,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = glim.likeCount.toString(),
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
