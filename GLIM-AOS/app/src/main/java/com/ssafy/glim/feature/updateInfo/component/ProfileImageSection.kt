package com.ssafy.glim.feature.updateInfo.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ssafy.glim.R

@Composable
fun ProfileImageSection(
    imageUri: String?,
    onImageClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUri)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.content_description_profile_image),
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f)
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp
                    )
                }
            },
            error = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.2f)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.content_description_profile_image),
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }
        )

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .align(Alignment.BottomCenter)
                .clickable { onImageClicked() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ImageSearch,
                contentDescription = stringResource(R.string.content_description_profile_image),
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
