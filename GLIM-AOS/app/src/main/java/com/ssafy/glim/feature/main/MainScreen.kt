package com.ssafy.glim.feature.main

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
import com.ssafy.glim.feature.auth.navigation.authNavGraph
import com.ssafy.glim.feature.bookdetail.navigation.bookDetailNavGraph
import com.ssafy.glim.feature.glimlist.navigation.glimListNavGraph
import com.ssafy.glim.feature.home.navigation.homeNavGraph
import com.ssafy.glim.feature.library.navigation.libraryNavGraph
import com.ssafy.glim.feature.main.component.MainBottomBar
import com.ssafy.glim.feature.post.navigation.postNavGraph
import com.ssafy.glim.feature.profile.navigation.profileNavGraph
import com.ssafy.glim.feature.reels.navigation.reelsNavGraph
import com.ssafy.glim.feature.updateInfo.navigation.updateInfoNavGraph
import kotlinx.collections.immutable.toImmutableList

@Composable
internal fun MainScreen(navigator: MainNavController = rememberMainNavController()) {
    Scaffold(
        bottomBar = {
            if(navigator.currentTab != MainTab.POST){
                MainBottomBar(
                    tabs = MainTab.entries.toImmutableList(),
                    currentTab = navigator.currentTab,
                    onTabSelected = {
                        navigator.navigate(it)
                    },
                    visible = navigator.shouldShowBottomBar(),
                )
            }
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
            authNavGraph(
                padding = innerPadding,
            )
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
            glimListNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )

            updateInfoNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
            )

            bookDetailNavGraph(
                padding = innerPadding,
                popBackStack = navigator::popBackStack,
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
