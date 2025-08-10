package com.ssafy.glim.feature.post.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import com.ssafy.glim.core.domain.model.Book
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.feature.post.PostRoute
import kotlin.reflect.typeOf

fun NavController.navigateToPost(navOptions: NavOptions) {
    navigate(BottomTabRoute.Post(), navOptions)
}

fun NavGraphBuilder.postNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<BottomTabRoute.Post>(
        typeMap = mapOf(
            typeOf<Book?>() to NavType.StringType
        )
    ) { backStackEntry ->
        PostRoute(
            bookId = backStackEntry.arguments?.getLong("bookId") ?: -1,
            padding = padding,
            popBackStack = popBackStack,
        )
    }
}
