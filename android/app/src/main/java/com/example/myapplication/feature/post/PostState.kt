import android.content.Context
import android.net.Uri
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

sealed interface PostSideEffect {
    data object NavigateBack : PostSideEffect

    data object ClearFocus : PostSideEffect

    data object OpenTextImagePicker : PostSideEffect

    data object OpenBackgroundImagePicker : PostSideEffect

    data class ShowToast(val message: String) : PostSideEffect
}

sealed interface PostIntent {
    data object Initialize : PostIntent

    data object OnBackPressed : PostIntent

    data object ConfirmExit : PostIntent

    data object CancelExit : PostIntent

    data class OnTextChanged(val text: String) : PostIntent

    data class OnFocusChanged(val focused: Boolean) : PostIntent

    data class OnTextImageSelected(val uri: Uri?, val context: Context) : PostIntent

    data class OnBackgroundImageSelected(val uri: Uri?) : PostIntent

    data object OnTextExtractionClick : PostIntent

    data object OnBackgroundImageClick : PostIntent

    data object OnCompleteClick : PostIntent

    data object OnClearFocusClick : PostIntent

    // Text Style Intents
    data object IncreaseFontSize : PostIntent

    data object DecreaseFontSize : PostIntent

    data object ToggleBold : PostIntent

    data object ToggleItalic : PostIntent
}

data class PostState(
    val recognizedText: String = "",
    val selectedImageUri: Uri? = null,
    val backgroundImageUri: Uri? = null,
    val isProcessing: Boolean = false,
    val showExitDialog: Boolean = false,
    val isFocused: Boolean = false,
    val textStyle: TextStyleState = TextStyleState(),
    val isLoading: Boolean = false,
    val error: String? = null,
)

data class TextStyleState(
    val fontSize: Float = 16f,
    val isBold: Boolean = false,
    val isItalic: Boolean = false,
) {
    val fontSizeUnit: TextUnit get() = fontSize.sp
    val fontWeight: FontWeight get() = if (isBold) FontWeight.Bold else FontWeight.Normal
    val fontStyle: FontStyle get() = if (isItalic) FontStyle.Italic else FontStyle.Normal
}
