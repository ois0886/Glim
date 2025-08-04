package com.ssafy.glim.feature.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.setting.LockScreenSettings

@Composable
fun LockScreenSection(
    settings: LockScreenSettings,
    onLockScreenGlimToggle: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(text = stringResource(R.string.lockscreen_section_title))

        SettingsToggleItem(
            title = stringResource(R.string.lockscreen_glim_title),
            description = stringResource(R.string.lockscreen_glim_description),
            checked = settings.glimEnabled,
            onCheckedChange = onLockScreenGlimToggle
        )
    }
}
