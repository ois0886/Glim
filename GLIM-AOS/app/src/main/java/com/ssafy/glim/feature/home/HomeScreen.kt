package com.ssafy.glim.feature.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun HomeRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    Text(
        text = "home",
    )
}
