package com.example.myapplication.feature.auth.login.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.GlimColor.GoogleColor
import com.example.myapplication.ui.theme.GlimColor.KakaoColor
import com.example.myapplication.ui.theme.GlimColor.NaverColor

enum class SocialProvider { GOOGLE, KAKAO, NAVER }

@Composable
fun SocialButton(
    provider: SocialProvider,
    onClick: () -> Unit,
) {
    val (iconRes, bgColor) =
        when (provider) {
            SocialProvider.GOOGLE -> R.drawable.ic_google to GoogleColor
            SocialProvider.KAKAO -> R.drawable.ic_kakao to KakaoColor
            SocialProvider.NAVER -> R.drawable.ic_naver to NaverColor
        }

    Surface(
        shape = CircleShape,
        border = BorderStroke(1.dp, Color.LightGray),
        color = bgColor,
        modifier =
            Modifier
                .size(48.dp)
                .clickable(onClick = onClick),
    ) {
        Image(
            painter = painterResource(iconRes),
            contentDescription = provider.name,
            modifier = Modifier.padding(14.dp),
        )
    }
}
