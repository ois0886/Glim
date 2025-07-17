package com.example.myapplication.feature.auth.signup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressIndicatorBar(progress: Float) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(Color.LightGray),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(progress)
                    .background(MaterialTheme.colorScheme.primary),
        )
    }
}
