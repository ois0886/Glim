package com.ssafy.glim.feature.library.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun <T> SearchFilterChip(
    modifier: Modifier = Modifier,
    filters: List<T>,
    selectedFilter: T,
    onFilterSelected: (T) -> Unit,
    toText: T.() -> String
) {
    LazyRow(
        modifier = modifier.padding(horizontal = 8.dp),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(filters) { filter ->
            FilterChip(
                text = filter.toText(),
                isSelected = filter == selectedFilter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

@Composable
private fun FilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val animatedScale by animateFloatAsState(
        targetValue = if (isSelected) 1.05f else 1f,
        animationSpec = tween(200)
    )

    Surface(
        modifier = modifier
            .scale(animatedScale)
            .clickable { onClick() },
        shape = RoundedCornerShape(50),
        color = if (isSelected) {
            Color(0xff4A90E2)
        } else {
            Color.White
        },
        border = if (!isSelected) {
            BorderStroke(1.dp, Color(0xffE0E0E0))
        } else {
            null
        },
        shadowElevation = if (isSelected) 4.dp else 0.dp,
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 24.dp,
                vertical = 12.dp
            ),
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) {
                Color.White
            } else {
                Color.Black
            }
        )
    }
}
