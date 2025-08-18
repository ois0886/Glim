package com.ssafy.glim.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.ssafy.glim.ui.theme.Typography

/**
 * GlimTopBar offers a sleek, customizable top app bar.
 * Parameters:
 * @param title       The text label displayed as the title.
 * @param showBack    Whether to show a back navigation icon.
 * @param onBack      Callback invoked when the back icon is pressed.
 * @param alignment   Alignment of the title: centered or left-aligned.
 * The bar renders with a fully transparent background,
 * letting underlying content or background bleed through.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlimTopBar(
    title: String = "",
    showBack: Boolean = false,
    onBack: () -> Unit = {},
    alignment: TitleAlignment = TitleAlignment.Center,
) {
    when (alignment) {
        TitleAlignment.Center -> {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = title,
                        style = Typography.titleSmall
                    )
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
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
                            style = Typography.titleMedium
                        )
                    }
                },
                navigationIcon = {
                    if (showBack) {
                        IconButton(onClick = onBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null
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
