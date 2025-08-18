package com.ssafy.glim.feature.celebrations

import androidx.lifecycle.ViewModel
import com.ssafy.glim.core.navigation.Navigator
import com.ssafy.glim.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CelebrationsViewModel @Inject constructor(
    private val navigator: Navigator,
) : ViewModel(), ContainerHost<CelebrationsUiState, CelebrationsSideEffect> {

    override val container = container<CelebrationsUiState, CelebrationsSideEffect>(CelebrationsUiState())

    fun startCelebration(nickname: String) = intent {
        reduce { state.copy(nickname = nickname, isLoading = true) }

        delay(3000)

        reduce { state.copy(isLoading = false) }
        postSideEffect(CelebrationsSideEffect.NavigateToLogin)
    }

    fun navigateToHome() = intent {
        navigator.navigateAndClearBackStack(Route.Login)
    }
}
