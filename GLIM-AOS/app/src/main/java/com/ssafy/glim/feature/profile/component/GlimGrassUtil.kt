package com.ssafy.glim.feature.profile.component

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val KST: ZoneId = ZoneId.of("Asia/Seoul")
private val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
private val ISO_FMT: DateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

private fun String.toLocalDate(): LocalDate? =
    runCatching { LocalDate.parse(this, ISO_FMT) }
        .recoverCatching { LocalDate.parse(this, DATE_FMT) }
        .getOrNull()

fun LocalDate.formatKey(): String = this.format(DATE_FMT)

/** ISO 또는 yyyy-MM-dd 파싱, 실패 시 오늘−6일 반환 */
fun parseJoinDate(joinDateStr: String): LocalDate =
    joinDateStr.toLocalDate() ?: LocalDate.now(KST).minusDays(6)

/** KST 기준 오늘 */
fun todayKst(): LocalDate = LocalDate.now(KST)

/** start…end 사이의 날짜 리스트 */
fun generateDayList(start: LocalDate, end: LocalDate): List<LocalDate> {
    val days = ChronoUnit.DAYS.between(start, end).toInt()
    return (0..days).map { start.plusDays(it.toLong()) }
}

/** 그리드: 주차별로 LocalDate or null 배치 */
fun buildGlimGrid(days: List<LocalDate>): List<List<LocalDate?>> {
    if (days.isEmpty()) return emptyList()
    val firstDow = (days.first().dayOfWeek.value + 6) % 7
    val totalCells = ((firstDow + days.size + 6) / 7) * 7
    val weeks = totalCells / 7
    return List(weeks) { w ->
        List(7) { d ->
            val idx = w * 7 + d - firstDow
            days.getOrNull(idx)
        }
    }
}

/** 주차별 첫 비-널 날짜의 monthValue를 레이블로 */
fun createMonthLabels(grid: List<List<LocalDate?>>): List<String> =
    grid.mapIndexed { i, week ->
        week.firstOrNull { it != null }?.monthValue
            ?.takeIf { i == 0 || it != grid[i - 1].firstOrNull()?.monthValue }
            ?.toString() ?: ""
    }

/** "YYYY" 또는 "YYYY ~ YYYY" */
fun formatYearLabel(joinDate: LocalDate, today: LocalDate): String =
    joinDate.year.let { start ->
        today.year.let { end ->
            if (start == end) "$start" else "$start ~ $end"
        }
    }
