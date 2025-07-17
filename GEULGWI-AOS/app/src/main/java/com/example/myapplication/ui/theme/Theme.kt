package com.example.myapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/*
* 각각 컬러들이 어떻게 쓰이는지 명시
* */
private val LightColorScheme =
    lightColorScheme(
        // 앱의 주요 브랜드 색상, 주요 액션 버튼 등에 사용
        primary = GlimColor.LightPrimary500,
        // primary 색상 위의 텍스트나 아이콘 색상
        onPrimary = Color.White,
        // primary 색상의 더 연한 버전, 백그라운드나 강조 영역에 사용
        primaryContainer = GlimColor.LightPrimary100,
        // primaryContainer 위의 텍스트나 아이콘 색상
        onPrimaryContainer = GlimColor.LightPrimary900,
        // 보조 액션이나 정보를 위한 색상
        secondary = GlimColor.LightBlueGreen,
        // secondary 색상 위의 텍스트나 아이콘 색상
        onSecondary = GlimColor.LightGray700,
        // secondary 색상의 더 연한 버전, 보조 컨테이너에 사용
        secondaryContainer = GlimColor.LightGray400,
        // secondaryContainer 위의 텍스트나 아이콘 색상
        onSecondaryContainer = GlimColor.LightGray400,
        // 대비를 위한 액센트 색상
        tertiary = GlimColor.LightPrimary50,
        // tertiary 색상 위의 텍스트나 아이콘 색상
        onTertiary = Color.White,
        // tertiary 색상의 더 연한 버전
        tertiaryContainer = Color.White,
        // tertiaryContainer 위의 텍스트나 아이콘 색상
        onTertiaryContainer = Color.White,
        // 오류 표시를 위한 색상
        error = GlimColor.LightRed,
        // error 색상 위의 텍스트나 아이콘 색상
        onError = Color.White,
        // error 색상의 더 연한 버전, 오류 메시지 배경 등에 사용
        errorContainer = GlimColor.LightPrimary100,
        // errorContainer 위의 텍스트나 아이콘 색상
        onErrorContainer = GlimColor.LightPrimary900,
        // 앱의 배경색
        background = Color.White,
        // background 위의 텍스트나 아이콘 색상
        onBackground = GlimColor.LightGray900,
        // 카드, 시트 등 표면 요소의 색상
        surface = Color.White,
        // surface 위의 텍스트나 아이콘 색상
        onSurface = GlimColor.LightGray500,
        // surface의 변형, 비활성화된 요소나 구분선에 사용
        surfaceVariant = GlimColor.LightGray600,
        // surface 색상에 적용되는 색조
        surfaceTint = GlimColor.LightRed,
        // surfaceVariant 위의 텍스트나 아이콘 색상
        onSurfaceVariant = GlimColor.LightGray600,
        // 경계선이나 구분선 등에 사용되는 색상
        outline = GlimColor.LightGray200,
        // outline의 변형, 덜 강조된 경계선이나 구분선에 사용
        outlineVariant = GlimColor.LightGray300,
        // 배경을 어둡게 하는 반투명 오버레이, 모달 다이얼로그에 사용
        scrim = GlimColor.LightGray300,
        // 높은 강조도의 표면 색상, 강조 요소나 카드에 사용
        surfaceBright = GlimColor.LightGray100,
        // 기본 표면 컨테이너, 카드나 다이얼로그 배경에 사용
        surfaceContainer = Color.White,
        // 높은 대비의 표면 컨테이너, 중요 정보나 액션을 담는 컨테이너
        surfaceContainerHigh = GlimColor.LightPrimary50,
        // 낮은 대비의 표면 컨테이너, 부차적인 정보나 배경 요소에 사용
        surfaceContainerLow = GlimColor.LightGray200,
        // 역전된 표면 색상, 어두운 배경 위 밝은 요소를 위해 사용
        inverseSurface = GlimColor.LightGray100,
        // inverseSurface 위의 텍스트나 밝은 강조에 사용
        inverseOnSurface = GlimColor.LightGray600,
    )

// Dark Mode 대응 안 할 예정
private val DarkColorScheme =
    darkColorScheme(
        onPrimary = Color.Black,
        onSecondary = Color.Black,
        onTertiary = Color.Black,
        background = Color(0xFF1C1B1F),
        onBackground = Color(0xFFE6E1E5),
        surface = Color(0xFF1C1B1F),
        onSurface = Color(0xFFE6E1E5),
        surfaceVariant = Color(0xFF49454F),
        onSurfaceVariant = Color(0xFFCAC4D0),
        outline = Color(0xFF938F99),
        inverseSurface = Color(0xFFE6E1E5),
        inverseOnSurface = Color(0xFF313033),
        error = Color(0xFFF2B8B5),
        onError = Color(0xFF601410),
        errorContainer = Color(0xFF8C1D18),
        onErrorContainer = Color(0xFFF9DEDC),
    )

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> LightColorScheme
            else -> LightColorScheme
        }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
