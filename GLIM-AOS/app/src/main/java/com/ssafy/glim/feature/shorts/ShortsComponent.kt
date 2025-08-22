package com.ssafy.glim.feature.shorts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Quote
import com.ssafy.glim.core.ui.GlimSubcomposeAsyncImage
import com.ssafy.glim.core.util.rememberCaptureAction
import com.ssafy.glim.feature.post.component.DarkGrayRoundedSurface
import com.ssafy.glim.ui.theme.GlimColor.LightGray300

@Composable
fun QuoteItem(
    quote: Quote,
    modifier: Modifier = Modifier,
    onLikeClick: () -> Unit = {},
    onShareClick: () -> Unit = {},
    onInstagramShareClick: () -> Unit = {},
    onMoreClick: () -> Unit = {},
    onBookInfoClick: (Long?) -> Unit
) {
    val imageGraphicsLayer = rememberGraphicsLayer()

    val captureAction = rememberCaptureAction(
        graphicsLayer = imageGraphicsLayer,
        fileName = "Quote_${System.currentTimeMillis()}.jpg",
    )

    Box(modifier = modifier) {
        GlimSubcomposeAsyncImage(
            context = LocalContext.current,
            imageUrl = quote.quoteImageUrl,
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    onDrawWithContent {
                        // AsyncImage만 GraphicsLayer에 기록
                        imageGraphicsLayer.record {
                            this@onDrawWithContent.drawContent()
                        }
                        drawLayer(imageGraphicsLayer)
                    }
                }
        )

        Column(
            modifier =
            Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                modifier = Modifier
                    .systemBarsPadding()
                    .padding(vertical = 16.dp, horizontal = 8.dp),
                onClick = { captureAction() }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_download),
                    contentDescription = stringResource(R.string.download),
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Column(
                modifier = Modifier.padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IconButton(onClick = onLikeClick) {
                    Icon(
                        painter =
                        painterResource(
                            if (quote.isLike) {
                                R.drawable.ic_favorite_fill
                            } else {
                                R.drawable.ic_favorite
                            },
                        ),
                        contentDescription = stringResource(R.string.like),
                        tint = if (quote.isLike) Color.Red else Color.White,
                    )
                }
                Text(
                    "${quote.likes}",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
            }

            // 공유 버튼 - com.ssafy.glim.core.util.ShareWithImageManager 활용
            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = onShareClick
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = stringResource(R.string.share),
                )
            }

            IconButton(
                modifier = Modifier.padding(horizontal = 8.dp),
                onClick = {
//                    if (try {
//                            context.packageManager.getPackageInfo("com.instagram.android", 0)
//                            true
//                        } catch (e: PackageManager.NameNotFoundException) {
//                            false
//                        }) {
//                        val appStoreIntent = Intent(Intent.ACTION_VIEW, "https://play.google.com/store/apps/details?id=com.instagram.android".toUri())
//                        appStoreIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                        context.startActivity(appStoreIntent)
//                    }
//                    else {
                    onInstagramShareClick()
//                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_instagram),
                    contentDescription = stringResource(R.string.share),
                    modifier = Modifier.size(24.dp, 24.dp),
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            QuoteBookContent(
                modifier = Modifier.fillMaxWidth(),
                bookId = quote.bookId,
                author = quote.author,
                bookName = quote.bookTitle,
                bookCover = quote.bookCoverUrl,
                page = quote.page,
            ) {
                onBookInfoClick(quote.bookId)
            }
        }
    }
}

@Composable
fun QuoteBookContent(
    modifier: Modifier = Modifier,
    bookId: Long,
    author: String,
    bookName: String,
    bookCover: String,
    page: Int,
    onBookInfoClick: (Long?) -> Unit = {},
) {
    DarkGrayRoundedSurface(modifier = modifier) {
        Row(
            modifier =
            Modifier
                .padding(16.dp)
                .clickable { onBookInfoClick(bookId) },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(
                model = bookCover,
                contentDescription = null,
                modifier = Modifier.size(40.dp, 56.dp),
                alpha = 0.8f,
                contentScale = ContentScale.FillHeight,
                error = painterResource(R.drawable.ic_launcher_background),
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
            ) {
                Text(
                    text = author,
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    color = LightGray300,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = bookName,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
