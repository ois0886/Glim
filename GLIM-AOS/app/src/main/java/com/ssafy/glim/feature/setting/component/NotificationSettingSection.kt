package com.ssafy.glim.feature.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.Settings

@Composable
fun NotificationSettingSection(
    settings: Settings,
    onAllNotificationsToggle: (Boolean) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SectionTitle(text = stringResource(R.string.notification_section_title))

        SettingsToggleItem(
            title = stringResource(R.string.all_push_notifications_title),
            description = stringResource(R.string.all_push_notifications_description),
            checked = settings.allNotificationsEnabled,
            onCheckedChange = onAllNotificationsToggle
        )
    }
}
