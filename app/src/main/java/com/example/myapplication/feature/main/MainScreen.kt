package com.example.myapplication.feature.main

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.navigation.compose.NavHost
import com.example.myapplication.feature.auth.navigation.authNavGraph
import com.example.myapplication.feature.home.navigation.homeNavGraph
import com.example.myapplication.feature.library.navigation.libraryNavGraph
import com.example.myapplication.feature.main.component.MainBottomBar
import com.example.myapplication.feature.post.navigation.postNavGraph
import com.example.myapplication.feature.profile.navigation.profileNavGraph
import com.example.myapplication.feature.reels.navigation.reelsNavGraph
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MainScreen(navigator: MainNavController = rememberMainNavController()) {
    Scaffold(
        bottomBar = {
            MainBottomBar(
                tabs = MainTab.entries.toImmutableList(),
                currentTab = navigator.currentTab,
                onTabSelected = {
                    navigator.navigate(it)
                },
                visible = navigator.shouldShowBottomBar(),
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { innerPadding ->
        NavHost(
            navController = navigator.navController,
            startDestination = navigator.startDestination,
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            modifier = Modifier.fillMaxSize(),
        ) {
            homeNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )
            postNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )
            libraryNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )
            profileNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )
            reelsNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )
            authNavGraph(
                padding = innerPadding,
            )
        }
    }
}

@Composable
fun PaddingValues.excludeSystemBars(): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues()

    return PaddingValues(
        start = calculateStartPadding(layoutDirection),
        top = calculateTopPadding() - systemBarsPadding.calculateTopPadding(),
        end = calculateEndPadding(layoutDirection),
        bottom = calculateBottomPadding(),
    )
}
