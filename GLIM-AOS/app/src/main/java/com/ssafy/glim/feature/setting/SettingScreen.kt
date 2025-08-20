package com.ssafy.glim.feature.setting

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ssafy.glim.R
import com.ssafy.glim.core.ui.GlimTopBar
import com.ssafy.glim.core.ui.TitleAlignment
import com.ssafy.glim.feature.login.component.GlimButton
import com.ssafy.glim.feature.main.excludeSystemBars
import com.ssafy.glim.feature.setting.component.LockSettingSection
import com.ssafy.glim.feature.setting.component.NotificationSettingSection
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
internal fun SettingRoute(
    padding: PaddingValues,
    popBackStack: () -> Unit = {},
    viewModel: SettingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SettingSideEffect.ShowError -> Toast.makeText(
                context,
                context.getString(sideEffect.messageRes),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    SettingScreen(
        state = state,
        padding = padding,
        onBackClick = popBackStack,
        onSaveClicked = { viewModel.onSaveClicked() },
        onAllNotificationsToggle = { viewModel.onAllNotificationsToggle(it) },
        onLockScreenGlimToggle = { viewModel.onLockScreenGlimToggle(it) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    state: SettingUiState,
    padding: PaddingValues,
    onBackClick: () -> Unit,
    onSaveClicked: () -> Unit,
    onAllNotificationsToggle: (Boolean) -> Unit,
    onLockScreenGlimToggle: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding.excludeSystemBars())
            .imePadding()
    ) {
        GlimTopBar(
            title = stringResource(R.string.setting_title),
            showBack = true,
            onBack = onBackClick,
            alignment = TitleAlignment.Center
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(PaddingValues(16.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            NotificationSettingSection(
                settings = state.settings,
                onAllNotificationsToggle = onAllNotificationsToggle
            )

            Spacer(modifier = Modifier.height(36.dp))

            LockSettingSection(
                settings = state.settings,
                onLockScreenGlimToggle = onLockScreenGlimToggle
            )

            Spacer(modifier = Modifier.weight(1f))

            GlimButton(
                text = if (state.isLoading) {
                    stringResource(R.string.loading)
                } else {
                    stringResource(R.string.save)
                },
                onClick = onSaveClicked,
                enabled = !state.isLoading,
            )
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
            onAllNotificationsToggle = {},
            padding = PaddingValues(0.dp),
            onSaveClicked = {},
            onLockScreenGlimToggle = {}
        )
    }
}
