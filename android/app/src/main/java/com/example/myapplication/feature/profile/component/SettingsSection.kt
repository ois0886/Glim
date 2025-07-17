package com.example.myapplication.feature.profile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
internal fun SettingsSection(
    onPersonalInfoClick: () -> Unit,
    onAccountSettingsClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLogOutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingItem(
            title = stringResource(R.string.settings_personal_info),
            onClick = onPersonalInfoClick
        )

        SettingItem(
            title = stringResource(R.string.settings_account),
            onClick = onAccountSettingsClick
        )

        SettingItem(
            title = stringResource(R.string.settings_notification),
            onClick = onNotificationSettingsClick
        )

        SettingItem(
            title = stringResource(R.string.settings_logout),
            onClick = onLogOutClick
        )
    }
}

@Composable
internal fun SettingItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = stringResource(R.string.content_description_navigate),
            tint = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingsSection() {
    MaterialTheme {
        SettingsSection(
            onPersonalInfoClick = {},
            onAccountSettingsClick = {},
            onNotificationSettingsClick = {},
            onLogOutClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSettingItem() {
    MaterialTheme {
        SettingItem(
            title = "개인정보 변경",
            onClick = {}
        )
    }
}