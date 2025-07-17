package com.example.myapplication.feature.post

import android.view.View
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

object SystemBarController {
    @Composable
    fun SetDarkSystemBars() {
        val view = LocalView.current

        LaunchedEffect(view) {
            configureSystemBars(view, isDark = true)
        }
    }

    private fun configureSystemBars(
        view: View,
        isDark: Boolean,
    ) {
        val window = (view.context as android.app.Activity).window
        val controller = WindowCompat.getInsetsController(window, view)

        controller.isAppearanceLightStatusBars = !isDark
        controller.isAppearanceLightNavigationBars = !isDark

        window.setFlags(
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
        )
    }
}
