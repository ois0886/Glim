package com.ssafy.glim.feature.bookdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.bookdetail.BookDetailScreen

fun NavGraphBuilder.bookDetailNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.BookDetail> { backStackEntry ->
        val route = backStackEntry.toRoute<Route.BookDetail>()

        BookDetailScreen(
            padding = padding,
            popBackStack = popBackStack,
            bookId = route.bookId
        )
    }
}
