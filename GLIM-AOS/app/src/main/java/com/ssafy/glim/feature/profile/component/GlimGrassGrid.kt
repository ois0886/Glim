package com.ssafy.glim.feature.profile.component

import android.annotation.SuppressLint
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ssafy.glim.R
import com.ssafy.glim.core.domain.model.UploadQuote
import com.ssafy.glim.ui.theme.GlimColor.GrassEmpty
import com.ssafy.glim.ui.theme.GlimColor.GrassLevel1
import com.ssafy.glim.ui.theme.GlimColor.GrassLevel2
import com.ssafy.glim.ui.theme.GlimColor.GrassLevel3
import com.ssafy.glim.ui.theme.GlimColor.GrassTextPrimary
import com.ssafy.glim.ui.theme.GlimColor.GrassTextSecondary
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun GlimGrassGrid(
    modifier: Modifier = Modifier,
    uploadQuotes: List<UploadQuote>,
    firstUploadDateStr: String,
    error: Boolean = false
) {

    // 1. parse first upload date & today
    val firstUploadDate = parseFirstUploadDate(firstUploadDateStr)
    val today = todayKst()

    // 2. uploadQuotes를 날짜별로 그룹화
    val glimRecord = groupUploadQuotesByDate(uploadQuotes)

    // 3. days, grid, labels
    val days = generateDayList(firstUploadDate, today)
    val grid = buildGlimGrid(days)
    val monthLabels = createMonthLabels(grid)
    val yearLabel = formatYearLabel(firstUploadDate, today)
    val weekLabels = weekLabelStrings()

    // 4. streaks
    val (maxStreak, currentStreak) = calculateGlimStreak(
        record = glimRecord,
        start = firstUploadDate,
        end = today
    )

    val scrollState = rememberScrollState()
    LaunchedEffect(grid) { scrollState.scrollTo(scrollState.maxValue) }

    Column(modifier.fillMaxWidth()) {
        Text(
            text = yearLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        )
        MonthRow(monthLabels, scrollState)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
            WeekLabelColumn(weekLabels)
            GlimGrassGridContent(grid, scrollState, glimRecord, firstUploadDate, today)
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            if(error){
                Text(
                    text = stringResource(R.string.error_load_profile_failed),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Red,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            } else{
                GlimStreakSummary(maxStreak, currentStreak)
            }
        }
    }
}

@Composable
private fun MonthRow(monthLabels: List<String>, scrollState: ScrollState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 44.dp, bottom = 6.dp)
            .horizontalScroll(scrollState),
        verticalAlignment = Alignment.CenterVertically
    ) {
        monthLabels.forEachIndexed { idx, label ->
            Box(
                Modifier.size(18.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
            }
            Spacer(modifier = Modifier.width(if (idx != monthLabels.lastIndex) 6.dp else 14.dp))
        }
    }
}

@Composable
private fun WeekLabelColumn(weekLabels: List<String>) {
    Column(
        modifier = Modifier.padding(end = 4.dp, top = 8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        weekLabels.forEach { label ->
            Box(
                Modifier.size(width = 32.dp, height = 18.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text(label, style = MaterialTheme.typography.labelMedium, color = Color.Gray)
            }
        }
    }
}

@Composable
private fun GlimGrassGridContent(
    grid: List<List<LocalDate?>>,
    scrollState: ScrollState,
    glimRecord: Map<String, Int>,
    firstUploadDate: LocalDate,
    today: LocalDate
) {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .border(1.dp, GrassEmpty, RoundedCornerShape(4.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        grid.forEach { week ->
            Column(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                horizontalAlignment = Alignment.End
            ) {
                week.forEach { date ->
                    if (date == null || date.isBefore(firstUploadDate) || date.isAfter(today)) {
                        Box(
                            Modifier
                                .size(18.dp)
                                .background(Color.Transparent)
                        )
                    } else {
                        val key = date.format(fmt)
                        val count = glimRecord[key] ?: 0
                        GlimGrassCell(count)
                    }
                }
            }
        }
        Spacer(Modifier.weight(1f))
    }
}

@Composable
private fun GlimGrassCell(count: Int) {
    val color = getGlimGrassColor(count)
    val border = if (count == 0) {
        Modifier.border(1.dp, GrassEmpty, RoundedCornerShape(3.dp))
    } else {
        Modifier
    }
    Box(
        border
            .then(Modifier.size(18.dp))
            .background(color, RoundedCornerShape(3.dp))
    )
}

@Composable
private fun GlimStreakSummary(maxStreak: Int, currentStreak: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.glim_record_max_streak, maxStreak),
            style = MaterialTheme.typography.bodySmall,
            color = GrassTextPrimary
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = stringResource(R.string.glim_record_current_streak, currentStreak),
            style = MaterialTheme.typography.bodySmall,
            color = GrassTextSecondary
        )
    }
}

@Composable
private fun weekLabelStrings(): List<String> = listOf(
    stringResource(R.string.week_mon),
    stringResource(R.string.week_tue),
    stringResource(R.string.week_wed),
    stringResource(R.string.week_thu),
    stringResource(R.string.week_fri),
    stringResource(R.string.week_sat),
    stringResource(R.string.week_sun)
)

// --- Helper Functions ---

/**
 * UploadQuote 리스트를 날짜별로 그룹화하여 Map<String, Int> 반환
 * key: "yyyy-MM-dd", value: 해당 날짜의 업로드 개수
 */
fun groupUploadQuotesByDate(uploadQuotes: List<UploadQuote>): Map<String, Int> {
    return uploadQuotes
        .mapNotNull { quote ->
            // createdAt에서 날짜 부분만 추출 ("2025-08-05T10:53:05.371886" 형식)
            quote.createdAt.substringBefore('T').takeIf { it.isNotBlank() }
        }
        .groupingBy { it }
        .eachCount()
}

/**
 * 첫 업로드 날짜 파싱
 */
fun parseFirstUploadDate(firstUploadDateStr: String): LocalDate {
    return try {
        LocalDate.parse(firstUploadDateStr.substringBefore('T'))
    } catch (_: Exception) {
        LocalDate.now(ZoneId.of("Asia/Seoul"))
    }
}

/**
 * Glim 잔디 색상 결정 (업로드 개수 기준)
 * 0개: 흰색, 1-2개: 연한 초록, 3-5개: 중간 초록, 6개 이상: 진한 초록
 */
fun getGlimGrassColor(count: Int): Color = when {
    count == 0 -> Color.White
    count in 1..2 -> GrassLevel1
    count in 3..5 -> GrassLevel2
    count >= 6 -> GrassLevel3
    else -> Color.LightGray
}

/**
 * Glim 업로드 스트릭 계산
 * 연속으로 글림을 업로드한 날짜 수 계산
 */
fun calculateGlimStreak(
    record: Map<String, Int>,
    start: LocalDate,
    end: LocalDate
): Pair<Int, Int> {
    val validDates = record
        .filter { (_, count) -> count > 0 }
        .keys
        .toSet()

    var maxStreak = 0
    var currentStreakCount = 0
    var date = start

    // 최대 스트릭 계산 (start -> end)
    while (!date.isAfter(end)) {
        if (date.formatKey() in validDates) {
            currentStreakCount++
            maxStreak = maxOf(maxStreak, currentStreakCount)
        } else {
            currentStreakCount = 0
        }
        date = date.plusDays(1)
    }

    // 현재 스트릭 계산 (end -> start 역순)
    var currentStreak = 0
    date = end
    while (!date.isBefore(start) && date.formatKey() in validDates) {
        currentStreak++
        date = date.minusDays(1)
    }

    if (currentStreak > 0 && maxStreak == 0) maxStreak = 1

    return maxStreak to currentStreak
}

@Preview(showBackground = true, name = "EmptyUpload")
@Composable
fun PreviewEmptyUpload() {
    Surface(color = Color(0xFFF6F6F6)) {
        Box(Modifier.padding(16.dp)) {
            GlimGrassGrid(
                uploadQuotes = emptyList(),
                firstUploadDateStr = ""
            )
        }
    }
}

@Preview(showBackground = true, name = "OneMonthUpload")
@Composable
fun PreviewOneMonthUpload() = PreviewGlimGrassWithUpload(days = 30)

@Preview(showBackground = true, name = "ThreeMonthUpload")
@Composable
fun PreviewThreeMonthUpload() = PreviewGlimGrassWithUpload(days = 90)

@SuppressLint("DefaultLocale")
@Composable
private fun PreviewGlimGrassWithUpload(days: Int) {
    val today = LocalDate.now(ZoneId.of("Asia/Seoul"))
    val start = today.minusDays((days - 1).toLong())
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val mockUploadQuotes = (0 until days).flatMap { i ->
        val date = start.plusDays(i.toLong())
        val uploadCount = when {
            i % 7 == 0 || i % 7 == 6 -> (0..2).random() // 주말: 적게
            else -> (0..4).random() // 평일: 많게
        }

        (0 until uploadCount).map { j ->
            UploadQuote(
                quoteId = (i * 10 + j).toLong(),
                content = "오늘의 영감을 주는 글귀 $j",
                views = (0..500).random().toLong(),
                page = (1..300).random(),
                likeCount = (0..100).random().toLong(),
                createdAt = "${date.format(fmt)}T${
                    String.format(
                        "%02d",
                        (9..23).random()
                    )
                }:${String.format("%02d", (0..59).random())}:00.000000",
                liked = (0..4).random() == 0 // 20% 확률로 좋아요
            )
        }
    }

    Surface(color = Color(0xFFF6F6F6)) {
        Box(Modifier.padding(16.dp)) {
            GlimGrassGrid(
                uploadQuotes = mockUploadQuotes,
                firstUploadDateStr = start.format(fmt)
            )
        }
    }
}
