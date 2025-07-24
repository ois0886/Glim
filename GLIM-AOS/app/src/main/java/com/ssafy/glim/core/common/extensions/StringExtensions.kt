package com.ssafy.glim.core.common.extensions

import androidx.core.util.PatternsCompat
import java.util.Calendar

/**
 * 이메일 형식이 유효한지 확인합니다.
 * @return 유효한 이메일 형식이면 true, 아니면 false
 */
fun String.isValidEmail(): Boolean {
    return PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * 비밀번호 형식이 유효한지 확인합니다.
 * 영문 대/소문자, 숫자, 특수문자를 포함한 8~16자
 * @return 유효한 비밀번호 형식이면 true, 아니면 false
 */
fun String.isValidPassword(): Boolean {
    val passwordRegex =
        Regex(
            "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,16}$",
        )
    return passwordRegex.matches(this)
}

/**
 * 6자리 숫자 코드가 유효한지 확인합니다.
 * @return 유효한 코드 형식이면 true, 아니면 false
 */
fun String.isValidCode(): Boolean {
    val codeRegex = Regex("^[0-9]{6}$")
    return codeRegex.matches(this)
}

/**
 * 이름이 유효한지 확인합니다. (2~16자)
 * @return 유효한 이름 형식이면 true, 아니면 false
 */
fun String.isValidName(): Boolean {
    val nameRegex = Regex("^.{2,16}$")
    return nameRegex.matches(this)
}

/**
 * 생년월일 검증 결과를 나타내는 sealed class
 */
sealed class BirthDateValidation {
    object Valid : BirthDateValidation()

    object InvalidFormat : BirthDateValidation() // 8자리 숫자가 아님

    object InvalidYear : BirthDateValidation() // 연도 범위 오류 (1900~현재년도)

    object InvalidMonth : BirthDateValidation() // 월 범위 오류 (1~12)

    object InvalidDay : BirthDateValidation() // 일 범위 오류 (월별 최대일수 초과)

    object FutureDate : BirthDateValidation() // 미래 날짜
}

/**
 * 생년월일이 유효한지 상세하게 확인합니다. (YYYYMMDD 형식)
 * @return BirthDateValidation 결과
 */
fun String.validateBirthDateDetailed(): BirthDateValidation {
    // 8자리 숫자인지 확인
    if (this.length != 8 || !this.all { it.isDigit() }) {
        return BirthDateValidation.InvalidFormat
    }

    try {
        val year = this.substring(0, 4).toInt()
        val month = this.substring(4, 6).toInt()
        val day = this.substring(6, 8).toInt()

        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // 연도 범위 검사 (1900년 ~ 현재년도)
        if (year !in 1900..currentYear) {
            return BirthDateValidation.InvalidYear
        }

        // 월 범위 검사 (1~12)
        if (month !in 1..12) {
            return BirthDateValidation.InvalidMonth
        }

        // 일 범위 기본 검사 (1~31)
        if (day !in 1..31) {
            return BirthDateValidation.InvalidDay
        }

        // 월별 최대 일수 검사
        val maxDaysInMonth =
            when (month) {
                2 -> if (isLeapYear(year)) 29 else 28 // 2월 (윤년 고려)
                4, 6, 9, 11 -> 30 // 4, 6, 9, 11월은 30일
                else -> 31 // 1, 3, 5, 7, 8, 10, 12월은 31일
            }

        if (day > maxDaysInMonth) {
            return BirthDateValidation.InvalidDay
        }

        val currentDate = Calendar.getInstance()
        val inputDate =
            Calendar.getInstance().apply {
                set(year, month - 1, day)
            }

        if (inputDate.after(currentDate)) {
            return BirthDateValidation.FutureDate
        }

        return BirthDateValidation.Valid
    } catch (_: Exception) {
        return BirthDateValidation.InvalidFormat
    }
}

/**
 * 윤년인지 확인합니다.
 */
private fun isLeapYear(year: Int): Boolean {
    return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
}

/**
 * 문자열에서 숫자만 추출하고 최대 길이만큼 제한합니다.
 * @param maxLength 최대 길이
 * @return 숫자만 포함된 문자열
 */
fun String.extractDigits(maxLength: Int): String {
    return this.filter { it.isDigit() }.take(maxLength)
}

/**
 * YYYYMMDD 형식을 YYYY-MM-DD 형식으로 변환
 */
fun String.formatBirthDate(): String {
    return if (this.length == 8) {
        val year = this.substring(0, 4)
        val month = this.substring(4, 6)
        val day = this.substring(6, 8)
        "$year-$month-$day"
    } else {
        this
    }
}

/**
 * "남성", "여성"을 "MALE", "FEMALE"로 변환
 */
fun String.formatGender(): String {
    return when (this) {
        "남성" -> "MALE"
        "여성" -> "FEMALE"
        else -> this
    }
}

/**
 * 문자열을 쉼표로 구분된 가격 형식으로 변환합니다.
 */
fun String.toCommaSeparatedPrice(): String {
    return this.replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
}

fun String.toBirthDateList(): Result<List<Int>> {
    return runCatching {
        val parts = split("-")
        require(parts.size == 3) { "Birth date must be in YYYY-MM-DD format" }

        val year = parts[0].toInt()
        val month = parts[1].toInt()
        val day = parts[2].toInt()

        // 기본적인 유효성 검증
        require(year in 1900..2100) { "Year must be between 1900 and 2100" }
        require(month in 1..12) { "Month must be between 1 and 12" }
        require(day in 1..31) { "Day must be between 1 and 31" }

        listOf(year, month, day)
    }.fold(
        onSuccess = { Result.success(it) },
        onFailure = { exception ->
            val errorMessage =
                when (exception) {
                    is NumberFormatException -> "날짜 형식이 올바르지 않습니다. YYYY-MM-DD 형식으로 입력해주세요."
                    is IllegalArgumentException -> exception.message ?: "유효하지 않은 날짜입니다."
                    else -> "날짜 변환 중 오류가 발생했습니다."
                }
            Result.failure(Exception(errorMessage))
        },
    )
}
