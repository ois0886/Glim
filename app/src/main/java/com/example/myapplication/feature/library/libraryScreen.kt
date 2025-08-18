package com.example.myapplication.feature.library

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun LibraryRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    Text(
        text = "Library",
    )
}
