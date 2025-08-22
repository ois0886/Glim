package com.ssafy.glim.feature.signup.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TermsConsentContent(
    termsChecked: Boolean,
    privacyChecked: Boolean,
    marketingChecked: Boolean,
    allChecked: Boolean,
    onToggleTerms: (Boolean) -> Unit,
    onTogglePrivacy: (Boolean) -> Unit,
    onToggleMarketing: (Boolean) -> Unit,
    onToggleAll: (Boolean) -> Unit,
    onOpenTerms: () -> Unit,
    onOpenPrivacy: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        GlimCheckRow(
            text = "전체 동의",
            subText = "서비스 이용을 위한 필수 및 선택 항목을 한 번에 동의합니다.",
            checked = allChecked,
            onCheckedChange = onToggleAll
        )
        Spacer(Modifier.padding(top = 8.dp))

        GlimCheckRow(
            text = "[필수] 서비스 이용약관",
            checked = termsChecked,
            onCheckedChange = onToggleTerms
        )

        GlimCheckRow(
            text = "[필수] 개인정보 처리방침",
            actionText = "보기",
            onActionClick = onOpenPrivacy,
            checked = privacyChecked,
            onCheckedChange = onTogglePrivacy
        )

        GlimCheckRow(
            text = "[선택] 마케팅 정보 수신 동의",
            subText = "이벤트/혜택 알림 수신에 동의합니다.",
            checked = marketingChecked,
            onCheckedChange = onToggleMarketing
        )
    }
}

@Composable
private fun GlimCheckRow(
    text: String,
    subText: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    androidx.compose.material3.Surface(
        tonalElevation = 1.dp,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Column(
                Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                if (subText != null) {
                    Text(
                        text = subText,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
            if (actionText != null && onActionClick != null) {
                TextButton(onClick = onActionClick) {
                    Text(
                        actionText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
