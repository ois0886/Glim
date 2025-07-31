package com.ssafy.glim.feature.auth.signup.component

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ssafy.glim.R
import com.ssafy.glim.ui.theme.MyApplicationTheme

@Composable
fun CelebrationsContent(
    nickname: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = stringResource(id = R.string.welcome_message, nickname),
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))
        AsyncImage(
            model = "https://images.unsplash.com/photo-1492633423870-43d1cd2775eb?w=400&h=300&fit=crop&crop=faces",
            contentDescription = stringResource(id = R.string.welcome_image_description),
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
            error = painterResource(id = R.drawable.ic_launcher_foreground)
        )

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun CelebrationsContentPreview() {
    MyApplicationTheme {
        CelebrationsContent(
            nickname = "Test"
        )
    }
}

@Preview(showBackground = true, name = "긴 닉네임")
@Composable
fun CelebrationsContentLongNicknamePreview() {
    MyApplicationTheme {
        CelebrationsContent(
            nickname = "TestTest"
        )
    }
}

@Preview(showBackground = true, name = "다크 테마", uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CelebrationsContentDarkPreview() {
    MyApplicationTheme {
        CelebrationsContent(
            nickname = "TestTestTest"
        )
    }
}
