package com.ssafy.glim.core.data.mapper

import com.ssafy.glim.BuildConfig
import com.ssafy.glim.core.data.dto.response.UserResponse
import com.ssafy.glim.core.domain.model.user.Gender
import com.ssafy.glim.core.domain.model.user.User
import com.ssafy.glim.core.domain.model.user.UserStatus

fun UserResponse.toDomain() = User(
    id = memberId,
    email = email,
    nickname = nickname,
    birthDate = birthDate,
    gender = when (gender.uppercase()) {
        "MALE" -> Gender.MALE
        "FEMALE" -> Gender.FEMALE
        else -> Gender.MALE
    },
    status = when (status.uppercase()) {
        "ACTIVE" -> UserStatus.ACTIVE
        "INACTIVE" -> UserStatus.INACTIVE
        else -> UserStatus.ACTIVE
    },
    profileUrl = BuildConfig.BASE_URL + "/images/" + profileUrl
)
