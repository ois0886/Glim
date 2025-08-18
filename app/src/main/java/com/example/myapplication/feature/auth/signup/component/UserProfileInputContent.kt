package com.example.myapplication.feature.auth.signup.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.feature.auth.login.component.EmailInputTextField

@Composable
fun UserProfileInputContent(
    name: String,
    onNameChange: (String) -> Unit,
    birthYear: String,
    onBirthYearChange: (String) -> Unit,
    selectedGender: String?,
    onGenderSelect: (String) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
    ) {
        Text(
            text = stringResource(id = R.string.profile_title),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(id = R.string.profile_subtitle),
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(modifier = Modifier.height(32.dp))

        // 이름
        Text(text = stringResource(id = R.string.profile_label_name))
        Spacer(modifier = Modifier.height(8.dp))
        EmailInputTextField(
            value = name,
            onValueChange = onNameChange,
            label = stringResource(id = R.string.profile_hint_name),
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(id = R.string.profile_note_name),
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 출생년도
        Text(text = stringResource(id = R.string.profile_label_birth))
        Spacer(modifier = Modifier.height(8.dp))
        EmailInputTextField(
            value = birthYear,
            onValueChange = onBirthYearChange,
            label = stringResource(id = R.string.profile_hint_birth),
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 성별
        Text(text = stringResource(id = R.string.profile_label_gender))
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GenderSelectableButton(
                text = stringResource(id = R.string.profile_gender_male),
                isSelected = selectedGender == "남자",
                onClick = { onGenderSelect("남자") },
            )
            GenderSelectableButton(
                text = stringResource(id = R.string.profile_gender_female),
                isSelected = selectedGender == "여자",
                onClick = { onGenderSelect("여자") },
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileInputContentPreview() {
    var name by remember { mutableStateOf("") }
    var birthYear by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf<String?>(null) }

    UserProfileInputContent(
        name = name,
        onNameChange = { name = it },
        birthYear = birthYear,
        onBirthYearChange = { birthYear = it },
        selectedGender = gender,
        onGenderSelect = { gender = it },
    )
}
