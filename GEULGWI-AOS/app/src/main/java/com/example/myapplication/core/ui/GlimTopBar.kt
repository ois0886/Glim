package com.example.myapplication.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

/**
 * GlimTopBar offers a sleek, customizable top app bar.
 * Parameters:
 * @param title       The text label displayed as the title.
 * @param showBack    Whether to show a back navigation icon.
 * @param onBack      Callback invoked when the back icon is pressed.
 * @param alignment   Alignment of the title: centered or left-aligned.
 * @param titleColor  Color of the title text.
 * @param titleSize   Font size for the title text.
 *
 * The bar renders with a fully transparent background,
 * letting underlying content or background bleed through.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlimTopBar(
    title: String,
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    alignment: TitleAlignment = TitleAlignment.Center,
    titleColor: Color = MaterialTheme.colorScheme.onPrimary,
    titleSize: TextUnit = 20.sp,
) {
    when (alignment) {
        TitleAlignment.Center -> {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style =
                            TextStyle(
                                color = titleColor,
                                fontSize = titleSize,
                                fontWeight = FontWeight.SemiBold,
                            ),
                    )
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = titleColor,
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        }
        TitleAlignment.Left -> {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        Text(
                            text = title,
                            style =
                                TextStyle(
                                    color = titleColor,
                                    fontSize = titleSize,
                                    fontWeight = FontWeight.Medium,
                                ),
                        )
                    }
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = titleColor,
                            )
                        }
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                    ),
            )
        }
    }
}

enum class TitleAlignment {
    Center,
    Left,
}
