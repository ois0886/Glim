package com.example.myapplication.core.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun DarkThemeScreen(content: @Composable () -> Unit) {
    val systemUiController = rememberSystemUiController()
    val view = LocalView.current

    LaunchedEffect(view) {
        systemUiController.setSystemBarsColor(
            color = Color.Transparent,
            darkIcons = false,
        )
        systemUiController.setNavigationBarColor(
            color = Color.Black,
            darkIcons = false,
        )
    }

    DisposableEffect(view) {
        onDispose {
            systemUiController.setSystemBarsColor(
                color = Color.Transparent,
                darkIcons = true,
            )
            systemUiController.setNavigationBarColor(
                color = Color.White,
                darkIcons = true,
            )
        }
    }

    MaterialTheme(
        colorScheme = darkColorScheme(),
    ) {
        CompositionLocalProvider(LocalContentColor provides Color.White) {
            content()
        }
    }
}
