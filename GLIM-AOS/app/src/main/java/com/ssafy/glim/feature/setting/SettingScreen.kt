package com.ssafy.glim.feature.setting

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.setting.component.LockSettingSection
import com.ssafy.glim.feature.setting.component.NotificationSettingSection
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SettingRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit,
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingSideEffect.ShowToast -> Toast.makeText(
                context,
                context.getString(sideEffect.messageRes),
                Toast.LENGTH_SHORT
            ).show()

            is SettingSideEffect.ShowError -> Toast.makeText(
                context,
                context.getString(sideEffect.messageRes),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SettingScreen(
        state = state,
        onBackClick = popBackStack,
        onTimeRangeClick = { viewModel.onTimeRangeClick() },
        onAllNotificationsToggle = { viewModel.onAllNotificationsToggle(it) },
        onDoNotDisturbModeToggle = { viewModel.onDoNotDisturbModeToggle(it) },
        onDoNotDisturbTimeToggle = { viewModel.onDoNotDisturbTimeToggle(it) },
        onWeeklyScheduleToggle = { viewModel.onWeeklyScheduleToggle(it) },
        onLockScreenGlimToggle = { viewModel.onLockScreenGlimToggle(it) },
        modifier = Modifier.padding(padding)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    state: SettingUiState,
    onBackClick: () -> Unit,
    onTimeRangeClick: () -> Unit,
    onAllNotificationsToggle: (Boolean) -> Unit,
    onDoNotDisturbModeToggle: (Boolean) -> Unit,
    onDoNotDisturbTimeToggle: (Boolean) -> Unit,
    onWeeklyScheduleToggle: (Boolean) -> Unit,
    onLockScreenGlimToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            GlimTopBar(
                title = stringResource(R.string.setting_title),
                showBack = true,
                onBack = onBackClick,
                alignment = TitleAlignment.Center
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                NotificationSettingSection(
                    settings = state.notificationSettings,
                    onAllNotificationsToggle = onAllNotificationsToggle,
                    onDoNotDisturbModeToggle = onDoNotDisturbModeToggle,
                    onDoNotDisturbTimeToggle = onDoNotDisturbTimeToggle,
                    onWeeklyScheduleToggle = onWeeklyScheduleToggle,
                    onTimeRangeClick = onTimeRangeClick
                )
            }

            item {
                LockSettingSection(
                    settings = state.lockScreenSettings,
                    onLockScreenGlimToggle = onLockScreenGlimToggle
                )
            }
        }
    }
}

@Preview(name = "Setting Screen", showBackground = true)
@Composable
private fun PreviewSettingScreen() {
    MaterialTheme {
        SettingScreen(
            state = SettingUiState(),
            onBackClick = {},
            onTimeRangeClick = {},
            onAllNotificationsToggle = {},
            onDoNotDisturbModeToggle = {},
            onDoNotDisturbTimeToggle = {},
            onWeeklyScheduleToggle = {},
            onLockScreenGlimToggle = {}
        )
    }
}

@Preview(name = "Setting Screen - Options Enabled", showBackground = true)
@Composable
private fun PreviewSettingScreenEnabled() {
    MaterialTheme {
        SettingScreen(
            state = SettingUiState(
                notificationSettings = NotificationSettings(
                    doNotDisturbTimeEnabled = true,
                    weeklyNotificationsEnabled = true
                )
            ),
            onBackClick = {},
            onTimeRangeClick = {},
            onAllNotificationsToggle = {},
            onDoNotDisturbModeToggle = {},
            onDoNotDisturbTimeToggle = {},
            onWeeklyScheduleToggle = {},
            onLockScreenGlimToggle = {}
        )
    }
}
