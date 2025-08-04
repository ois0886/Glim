package com.ssafy.glim.feature.setting.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.feature.setting.NotificationSettings

@Composable
fun NotificationSection(
    settings: NotificationSettings,
    onAllNotificationsToggle: (Boolean) -> Unit,
    onDoNotDisturbModeToggle: (Boolean) -> Unit,
    onDoNotDisturbTimeToggle: (Boolean) -> Unit,
    onWeeklyScheduleToggle: (Boolean) -> Unit,
    onTimeRangeClick: () -> Unit
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

        SettingsToggleItem(
            title = stringResource(R.string.do_not_disturb_mode_title),
            description = stringResource(R.string.do_not_disturb_mode_description),
            checked = settings.doNotDisturbEnabled,
            onCheckedChange = onDoNotDisturbModeToggle
        )

        Column {
            SettingsToggleItem(
                title = stringResource(R.string.do_not_disturb_time_title),
                description = stringResource(R.string.do_not_disturb_time_description),
                checked = settings.doNotDisturbTimeEnabled,
                onCheckedChange = onDoNotDisturbTimeToggle
            )

            if (settings.doNotDisturbTimeEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                SettingsTimeItem(
                    label = stringResource(R.string.time_setting_label),
                    timeRange = settings.defaultTimeRange,
                    onClick = onTimeRangeClick
                )
            }
        }

        Column {
            SettingsToggleItem(
                title = stringResource(R.string.weekly_schedule_title),
                description = stringResource(R.string.weekly_schedule_description),
                checked = settings.weeklyNotificationsEnabled,
                onCheckedChange = onWeeklyScheduleToggle
            )

            if (settings.weeklyNotificationsEnabled) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    SettingsTimeItem(
                        label = stringResource(R.string.weekday_label),
                        timeRange = settings.weekdayTimeRange,
                        onClick = onTimeRangeClick
                    )
                    SettingsTimeItem(
                        label = stringResource(R.string.weekend_label),
                        timeRange = settings.weekendTimeRange,
                        onClick = onTimeRangeClick
                    )
                }
            }
        }
    }
}
