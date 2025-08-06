package com.ssafy.glim.feature.post.component.editabletext

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ssafy.glim.feature.post.TextStyleState
import com.ssafy.glim.ui.theme.FONT

enum class TextControllerType {
    NOTHING,
    FONT,
    COLOR
}

@Composable
fun TextConfigContent(
    textStyle: TextStyleState,
    onIncreaseFontSize: () -> Unit,
    onDecreaseFontSize: () -> Unit,
    onToggleBold: () -> Unit,
    onToggleItalic: () -> Unit,
    updateFontFamily: (FontFamily) -> Unit,
    updateTextColor: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showControllerType by remember { mutableStateOf(TextControllerType.NOTHING) }

    Column(
        modifier = modifier.imePadding().padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showControllerType == TextControllerType.COLOR) {
            TextColorController(updateTextColor)
        } else if (showControllerType == TextControllerType.FONT) {
            FontFamilyController(modifier, updateFontFamily)
        }

        Surface(
            modifier = modifier.padding(8.dp),
            color = Color.DarkGray.copy(alpha = 0.9f),
            shape = RoundedCornerShape(8.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TextSizeControls(
                    onIncreaseFontSize = onIncreaseFontSize,
                    onDecreaseFontSize = onDecreaseFontSize,
                )

                TextStyleControls(
                    isBold = textStyle.isBold,
                    isItalic = textStyle.isItalic,
                    onToggleBold = onToggleBold,
                    onToggleItalic = onToggleItalic,
                )

                FontFamilyControls(
                    openFontController = { showControllerType = TextControllerType.FONT }
                )

                TextColorControls(
                    openColorPalette = { showControllerType = TextControllerType.COLOR }
                )
            }
        }
    }
}

@Composable
private fun FontFamilyController(
    modifier: Modifier,
    updateFontFamily: (FontFamily) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items(FONT.entries) {
            Surface(
                modifier = modifier
                    .padding(8.dp)
                    .clickable { updateFontFamily(it.fontFamily) },
                color = Color.DarkGray.copy(alpha = 0.8f),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(
                    text = "가나다라",
                    style = MaterialTheme.typography.bodySmall.copy(fontFamily = it.fontFamily),
                    modifier = Modifier.padding(8.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun TextColorController(updateTextColor: (Color) -> Unit) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(postFontColorList) {
            Surface(
                modifier = Modifier
                    .clickable { updateTextColor(it) },
                color = Color.White.copy(alpha = 0.9f),
            ) {
                Box(
                    Modifier
                        .size(24.dp)
                        .padding(2.dp)
                        .background(it)
                )
            }
        }
    }
}
