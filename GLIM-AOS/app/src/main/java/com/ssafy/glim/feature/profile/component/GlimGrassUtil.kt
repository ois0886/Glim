package com.ssafy.glim.feature.profile.component

import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

private val KST: ZoneId = ZoneId.of("Asia/Seoul")
private val DATE_FMT: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun LocalDate.formatKey(): String = this.format(DATE_FMT)

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
        val datesInWeek = week.filterNotNull()
        val months = datesInWeek.map { it.monthValue }.distinct()

        when {
            i == 0 && months.isNotEmpty() -> months.first().toString()
            months.size > 1 -> months.last().toString()
            months.isNotEmpty() -> {
                val currentMonth = months.first()
                val prevMonth = if (i > 0) {
                    grid[i - 1].filterNotNull().lastOrNull()?.monthValue
                } else {
                    null
                }

                if (currentMonth != prevMonth) currentMonth.toString() else ""
            }
            else -> ""
        }
    }

/** "YYYY" 또는 "YYYY ~ YYYY" */
fun formatYearLabel(joinDate: LocalDate, today: LocalDate): String =
    joinDate.year.let { start ->
        today.year.let { end ->
            if (start == end) "$start" else "$start ~ $end"
        }
    }
