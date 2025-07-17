package com.example.myapplication.feature.auth.signup.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R

@Composable
fun UserProfileInputContent(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String? = null,
    birthYear: String,
    onBirthYearChange: (String) -> Unit,
    birthYearError: String? = null,
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
        TextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(id = R.string.profile_hint_name)) },
            isError = nameError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (nameError != null) {
            Text(
                text = nameError,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        } else {
            Text(
                text = stringResource(id = R.string.profile_note_name),
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 출생년도
        Text(text = stringResource(id = R.string.profile_label_birth))
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = birthYear,
            onValueChange = onBirthYearChange,
            label = { Text(stringResource(id = R.string.profile_hint_birth)) },
            isError = birthYearError != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        )

        if (birthYearError != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = birthYearError,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

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
        nameError = null,
        birthYear = birthYear,
        onBirthYearChange = { birthYear = it },
        birthYearError = null,
        selectedGender = gender,
        onGenderSelect = { gender = it },
    )
}

@Preview(showBackground = true, name = "With Errors")
@Composable
fun UserProfileInputContentPreviewWithErrors() {
    UserProfileInputContent(
        name = "A",
        onNameChange = {},
        nameError = "이름은 2~16자로 입력해주세요.",
        birthYear = "202",
        onBirthYearChange = {},
        birthYearError = "4자리 숫자로 입력해주세요.",
        selectedGender = null,
        onGenderSelect = {},
    )
}
