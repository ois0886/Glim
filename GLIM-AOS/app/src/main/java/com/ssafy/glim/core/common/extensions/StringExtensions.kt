package com.ssafy.glim.core.common.extensions

import androidx.compose.ui.text.input.TextFieldValue
import androidx.core.util.PatternsCompat
import com.ssafy.glim.core.domain.model.user.Gender
import java.util.Calendar

/**
 * TextFieldValue의 텍스트를 YYYY-MM-DD 형식으로 변환
 */
fun TextFieldValue.formatBirthDate(): List<Int> {
    return this.text.formatBirthDate()
}

// 기존 String 확장 함수들...

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

    object InvalidFormat : BirthDateValidation()

    object InvalidYear : BirthDateValidation()

    object InvalidMonth : BirthDateValidation()

    object InvalidDay : BirthDateValidation()

    object FutureDate : BirthDateValidation()
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
                2 -> if (isLeapYear(year)) 29 else 28
                4, 6, 9, 11 -> 30
                else -> 31
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
fun String.formatBirthDate(): List<Int> {
    return if (this.length == 8) {
        val year = this.substring(0, 4).toInt()
        val month = this.substring(4, 6).toInt()
        val day = this.substring(6, 8).toInt()
        listOf(year, month, day, 0, 0)
    } else {
        listOf(1999, 1, 1, 0, 0)
    }
}

/**
 * YYYYMMDD 형식을 YYYY-MM-DD 형식으로 변환
 */
fun String.formatBirthDateToNumber(): String {
    return try {
        val dateOnly = this.substringBefore("T")
        dateOnly.replace("-", "")
    } catch (_: Exception) {
        ""
    }
}

/**
 * YYYYMMDD 형식을 ISO "1999-01-01T00:00:00" 형식으로 변환
 */
fun String.formatBirthDateToISO(): String {
    return if (this.length == 8) {
        val year = this.substring(0, 4)
        val month = this.substring(4, 6)
        val day = this.substring(6, 8)
        "$year-$month-${day}T00:00:00"
    } else {
        "1999-01-01T00:00:00"
    }
}

/**
 * "남성", "여성"을 "MALE", "FEMALE"로 변환
 */
fun String.formatGender(): String {
    return if (this.equals("남자", ignoreCase = true)) {
        "MALE"
    } else if (this.equals("여자", ignoreCase = true)) {
        "FEMALE"
    } else {
        this
    }
}

/**
 * 문자열을 쉼표로 구분된 가격 형식으로 변환합니다.
 */
fun String.toCommaSeparatedPrice(): String {
    return this.replace(Regex("(\\d)(?=(\\d{3})+(?!\\d))"), "$1,")
}

/**
 * Gender enum을 한국어 문자열로 변환
 */
fun Gender.formatGenderToString(): String {
    return when (this) {
        Gender.MALE -> "남자"
        Gender.FEMALE -> "여자"
    }
}

fun String.parseHtmlString(): String {
    return this
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&amp;", "&")
        .replace("&quot;", "\"")
        .replace("&apos;", "'")
        .replace("&#39;", "'")
        .replace("&nbsp;", " ")
}
