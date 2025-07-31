package com.ssafy.glim.core.common.utils

import androidx.compose.ui.text.input.TextFieldValue
import com.ssafy.glim.core.common.extensions.BirthDateValidation
import com.ssafy.glim.core.common.extensions.isValidCode
import com.ssafy.glim.core.common.extensions.isValidEmail
import com.ssafy.glim.core.common.extensions.isValidName
import com.ssafy.glim.core.common.extensions.isValidPassword
import com.ssafy.glim.core.common.extensions.validateBirthDateDetailed

/**
 * 유효성 검사 결과를 나타내는 sealed class
 */
sealed class ValidationResult {
    object Valid : ValidationResult()

    data class Invalid(val errorMessageRes: Int) : ValidationResult()
}

/**
 * 유효성 검사 관련 유틸리티 클래스
 */
object ValidationUtils {
    /**
     * 이메일 유효성 검사 - TextFieldValue 버전
     */
    fun validateEmail(
        email: TextFieldValue,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            email.text.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !email.text.isValidEmail() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 이메일 유효성 검사 - String 버전
     */
    fun validateEmail(
        email: String,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !email.isValidEmail() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 비밀번호 유효성 검사 - TextFieldValue 버전
     */
    fun validatePassword(
        password: TextFieldValue,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            password.text.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !password.text.isValidPassword() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 비밀번호 유효성 검사 - String 버전
     */
    fun validatePassword(
        password: String,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !password.isValidPassword() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 코드 유효성 검사 - String 버전
     */
    fun validateCode(
        code: String,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            code.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !code.isValidCode() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 이름 유효성 검사 - String 버전
     */
    fun validateName(
        name: String,
        emptyErrorRes: Int,
        invalidErrorRes: Int,
    ): ValidationResult {
        return when {
            name.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            !name.isValidName() -> ValidationResult.Invalid(invalidErrorRes)
            else -> ValidationResult.Valid
        }
    }

    /**
     * 생년월일 유효성 검사 (YYYYMMDD 형식) - TextFieldValue 버전
     */
    fun validateBirthDate(
        birthDate: TextFieldValue,
        emptyErrorRes: Int,
        formatErrorRes: Int,
        yearErrorRes: Int,
        monthErrorRes: Int,
        dayErrorRes: Int,
        futureDateErrorRes: Int,
    ): ValidationResult {
        return when {
            birthDate.text.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            else -> {
                when (birthDate.text.validateBirthDateDetailed()) {
                    is BirthDateValidation.Valid -> ValidationResult.Valid
                    is BirthDateValidation.InvalidFormat -> ValidationResult.Invalid(formatErrorRes)
                    is BirthDateValidation.InvalidYear -> ValidationResult.Invalid(yearErrorRes)
                    is BirthDateValidation.InvalidMonth -> ValidationResult.Invalid(monthErrorRes)
                    is BirthDateValidation.InvalidDay -> ValidationResult.Invalid(dayErrorRes)
                    is BirthDateValidation.FutureDate -> ValidationResult.Invalid(futureDateErrorRes)
                }
            }
        }
    }

    /**
     * 생년월일 유효성 검사 (YYYYMMDD 형식) - String 버전
     */
    fun validateBirthDate(
        birthDate: String,
        emptyErrorRes: Int,
        formatErrorRes: Int,
        yearErrorRes: Int,
        monthErrorRes: Int,
        dayErrorRes: Int,
        futureDateErrorRes: Int,
    ): ValidationResult {
        return when {
            birthDate.isBlank() -> ValidationResult.Invalid(emptyErrorRes)
            else -> {
                when (birthDate.validateBirthDateDetailed()) {
                    is BirthDateValidation.Valid -> ValidationResult.Valid
                    is BirthDateValidation.InvalidFormat -> ValidationResult.Invalid(formatErrorRes)
                    is BirthDateValidation.InvalidYear -> ValidationResult.Invalid(yearErrorRes)
                    is BirthDateValidation.InvalidMonth -> ValidationResult.Invalid(monthErrorRes)
                    is BirthDateValidation.InvalidDay -> ValidationResult.Invalid(dayErrorRes)
                    is BirthDateValidation.FutureDate -> ValidationResult.Invalid(futureDateErrorRes)
                }
            }
        }
    }

    /**
     * 비밀번호 확인 유효성 검사 - TextFieldValue 버전
     */
    fun validatePasswordConfirm(
        password: TextFieldValue,
        confirmPassword: TextFieldValue,
        mismatchErrorRes: Int,
    ): ValidationResult {
        return if (confirmPassword.text.isNotBlank() && password.text != confirmPassword.text) {
            ValidationResult.Invalid(mismatchErrorRes)
        } else {
            ValidationResult.Valid
        }
    }

    /**
     * 비밀번호 확인 유효성 검사 - String 버전
     */
    fun validatePasswordConfirm(
        password: String,
        confirmPassword: String,
        mismatchErrorRes: Int,
    ): ValidationResult {
        return if (confirmPassword.isNotBlank() && password != confirmPassword) {
            ValidationResult.Invalid(mismatchErrorRes)
        } else {
            ValidationResult.Valid
        }
    }

    /**
     * 성별 선택 유효성 검사
     */
    fun validateGender(
        gender: String?,
        emptyErrorRes: Int,
    ): ValidationResult {
        return if (gender == null) {
            ValidationResult.Invalid(emptyErrorRes)
        } else {
            ValidationResult.Valid
        }
    }
}

fun ValidationResult.toErrorRes(): Int? = when (this) {
    is ValidationResult.Valid -> null
    is ValidationResult.Invalid -> this.errorMessageRes
}
