package com.ssafy.glim.feature.bookdetail.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.ssafy.glim.core.navigation.Route
import com.ssafy.glim.feature.bookdetail.BookDetailScreen

fun NavGraphBuilder.bookDetailNavGraph(
    padding: PaddingValues,
    popBackStack: () -> Unit,
) {
    composable<Route.BookDetail> { backStackEntry ->
        BookDetailScreen(
            padding = padding,
            popBackStack = popBackStack,
            bookId = backStackEntry.arguments?.getLong("bookId") ?: 0L,
        )
    }
}
