package com.ssafy.glim.feature.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.ssafy.glim.R

@Composable
internal fun ProfileHeader(
    profileImageUrl: String?,
    userName: String,
    modifier: Modifier = Modifier,
    error: Boolean = false
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(16.dp))

        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(profileImageUrl ?: R.drawable.base_profile)
                .crossfade(true)
                .build(),
            contentDescription = stringResource(R.string.content_description_profile_image),
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            loading = {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(
                            color = Color.Gray.copy(alpha = 0.1f),
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 2.dp,
                    )
                }
            },
            error = {
                // 에러 시에도 기본 이미지 표시
                SubcomposeAsyncImage(
                    model = R.drawable.base_profile,
                    contentDescription = stringResource(R.string.content_description_profile_image),
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = {
                        // 기본 이미지도 로드 실패 시 아이콘 표시
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .background(
                                    color = Color.Gray.copy(alpha = 0.2f),
                                    shape = CircleShape,
                                ),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = stringResource(R.string.content_description_profile_image),
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp),
                            )
                        }
                    }
                )
            },
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (error) {
                Text(
                    text = stringResource(R.string.error_load_profile_failed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Black,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileHeader() {
    MaterialTheme {
        ProfileHeader(
            profileImageUrl = null,
            userName = "박성준",
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewProfileHeaderWithError() {
    MaterialTheme {
        ProfileHeader(
            profileImageUrl = null,
            userName = "박성준",
            error = true
        )
    }
}
