package com.ssafy.glim.core.ui

import android.content.Context
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun GlimSubcomposeAsyncImage(
    context: Context,
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    SubcomposeAsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = null,
        modifier = modifier.size(width = 240.dp, height = 360.dp),
        contentScale = ContentScale.Crop,
        imageLoader = context.imageLoader,
        loading = {
            GlimLoader(Modifier)
        },
        error = {
            GlimErrorLoader(Modifier)
        }
    )
}
