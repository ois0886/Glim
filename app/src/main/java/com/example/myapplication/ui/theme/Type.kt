package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

val glimDefaultFont =
    FontFamily(
        Font(R.font.gowun_batang_regular, FontWeight.Normal),
        Font(R.font.gowun_batang_bold, FontWeight.Bold),
    )

// Set of Material typography styles to start with
val Typography =
    Typography(
        headlineLarge =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 40.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineMedium =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 36.sp,
                letterSpacing = 0.5.sp,
            ),
        headlineSmall =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 32.sp,
                letterSpacing = 0.5.sp,
            ),
        titleLarge =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 28.sp,
                letterSpacing = 0.5.sp,
            ),
        titleMedium =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.5.sp,
            ),
        titleSmall =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 20.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyLarge =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                letterSpacing = 0.5.sp,
            ),
        bodyMedium =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                letterSpacing = 0.5.sp,
            ),
        bodySmall =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
            ),
        labelLarge =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                letterSpacing = 0.5.sp,
            ),
        labelMedium =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                letterSpacing = 0.5.sp,
            ),
        labelSmall =
            TextStyle(
                fontFamily = glimDefaultFont,
                fontWeight = FontWeight.Normal,
                fontSize = 10.sp,
                letterSpacing = 0.5.sp,
            ),
    )
