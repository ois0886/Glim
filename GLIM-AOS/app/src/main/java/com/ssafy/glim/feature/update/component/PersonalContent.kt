import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.ssafy.glim.core.common.extensions.formatBirthDateToNumber
import com.ssafy.glim.feature.auth.signup.component.UserProfileInputContent
import com.ssafy.glim.feature.update.UpdateInfoUiState
import com.ssafy.glim.feature.update.component.EmailSection
import com.ssafy.glim.feature.update.component.ProfileImageSection

@Composable
fun PersonalInfoContent(
    state: UpdateInfoUiState,
    onNameChanged: (TextFieldValue) -> Unit,
    onProfileImageClicked: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        ProfileImageSection(
            imageUri = state.profileImageUri,
            onImageClicked = onProfileImageClicked,
        )

        Spacer(modifier = Modifier.height(24.dp))

        EmailSection(state.email)

        UserProfileInputContent(
            isUpdate = true,
            name = state.newName,
            onNameChange = onNameChanged,
            nameError = null,
            birthYear = TextFieldValue(state.birthDate.formatBirthDateToNumber()),
            onBirthYearChange = { /* 수정 모드에서는 사용되지 않음 */ },
            birthYearError = null,
            selectedGender = state.gender,
            onGenderSelect = { /* 수정 모드에서는 사용되지 않음 */ }
        )
    }
}
