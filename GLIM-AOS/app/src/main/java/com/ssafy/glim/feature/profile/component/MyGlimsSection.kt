package com.ssafy.glim.feature.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.ssafy.glim.R

@Composable
internal fun MyGlimsSection(
    navigateToGlimUploadList: () -> Unit,
    navigateToGlimLikedList: () -> Unit,
    publishedGlimCount: Int,
    likedGlimCount: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        MyGlimsItem(
            count = publishedGlimCount,
            label = stringResource(R.string.profile_recent_articles),
            onClick = navigateToGlimUploadList
        )

        MyGlimsItem(
            count = likedGlimCount,
            label = stringResource(R.string.profile_liked_articles),
            onClick = navigateToGlimLikedList
        )
    }
}

@Composable
private fun MyGlimsItem(
    count: Int,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = count.toString(),
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Text(
            text = label,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewMyGlimsSection() {
    MaterialTheme {
        MyGlimsSection(
            navigateToGlimUploadList = {},
            navigateToGlimLikedList = {},
            publishedGlimCount = 24,
            likedGlimCount = 8
        )
    }
}