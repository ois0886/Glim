package com.ssafy.glim.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import coil.request.repeatCount

@Composable
fun GifDisplay(
    modifier: Modifier = Modifier,
    gifResourceId: Int,
    contentScale: ContentScale = ContentScale.Fit,
    repeatCount: Int = -1,
    contentDescription: String? = "GIF Animation"
) {
    val context = LocalContext.current

    val imageLoader = ImageLoader.Builder(context)
        .components {
            add(GifDecoder.Factory())
        }
        .memoryCache {
            coil.memory.MemoryCache.Builder(context)
                .maxSizePercent(0.75)
                .build()
        }
        .diskCache {
            coil.disk.DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02)
                .build()
        }
        .build()

    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(gifResourceId)
            .repeatCount(repeatCount)
            .crossfade(true)
            .build(),
        contentDescription = contentDescription,
        imageLoader = imageLoader,
        modifier = modifier,
        contentScale = contentScale
    )
}
