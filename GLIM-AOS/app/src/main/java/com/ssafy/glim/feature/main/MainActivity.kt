package com.ssafy.glim.feature.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ssafy.glim.core.navigation.LaunchedNavigator
import com.ssafy.glim.ui.theme.MyApplicationTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navigator: MainNavController = rememberMainNavController()
            LaunchedNavigator(navigator.navController)
            MyApplicationTheme {
                MainScreen(
                    navigator = navigator,
                )
            }
        }
    }
}
