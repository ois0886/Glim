package com.ssafy.glim.feature.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.glim.core.navigation.BottomTabRoute
import com.ssafy.glim.core.navigation.Navigator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MainViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel() {
    fun navigateHome() = viewModelScope.launch {
        navigator.navigate(route = BottomTabRoute.Home)
    }

    fun navigateQuote(quoteId: Long) = viewModelScope.launch {
        navigator.navigate(
            route = BottomTabRoute.Shorts(quoteId),
            saveState = true,
            launchSingleTop = true,
        )
    }

    fun navigatePost() = viewModelScope.launch {
        navigator.navigate(
            route = BottomTabRoute.Post(),
            saveState = true,
            launchSingleTop = true,
        )
    }

    fun navigateSearch() = viewModelScope.launch {
        navigator.navigate(
            route = BottomTabRoute.Search,
            saveState = true,
            launchSingleTop = true,
        )
    }

    fun navigateProfile() = viewModelScope.launch {
        navigator.navigate(
            route = BottomTabRoute.Profile,
            saveState = true,
            launchSingleTop = true,
        )
    }
}
