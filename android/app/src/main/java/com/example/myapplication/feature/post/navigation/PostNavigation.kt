package com.example.myapplication.feature.post.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.example.myapplication.core.navigation.BottomTabRoute
import com.example.myapplication.feature.post.PostRoute

fun NavController.navigateToPost(navOptions: NavOptions) {
    navigate(BottomTabRoute.Post, navOptions)
}

fun NavGraphBuilder.postNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Post> {
        PostRoute(
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
