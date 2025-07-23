package com.ssafy.glim.core.data.repository

import com.ssafy.glim.core.common.extensions.toBirthDateList
import com.ssafy.glim.core.data.dto.request.LoginRequest
import com.ssafy.glim.core.data.dto.request.SignUpRequest
import com.ssafy.glim.core.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        // TODO: 추가하기 private val dataSource: AuthRemoteDataSource,
    ) : AuthRepository {
        private val mockRegisteredEmails =
            setOf(
                "test@example.com",
                "user@test.com",
                "admin@sample.com",
            )

        private val mockValidAccounts =
            mapOf(
                "test@example.com" to "password123",
                "user@test.com" to "123456",
                "admin@sample.com" to "admin123",
            )

        override fun signUp(
            email: String,
            nickname: String,
            password: String,
            gender: String,
            birthDate: String,
        ): Flow<Result<Unit>> =
            flow {
                val result =
                    runCatching {
                        val birthDateResult = birthDate.toBirthDateList()
                        if (birthDateResult.isFailure) {
                            throw Exception("올바르지 않은 생년월일 형식입니다.")
                        }

                        val birthDateList = birthDateResult.getOrThrow()
                        val request =
                            SignUpRequest(
                                email = email,
                                nickname = nickname,
                                password = password,
                                gender = gender,
                                birthDate = birthDateList,
                            )

                        when {
                            mockRegisteredEmails.contains(email) -> {
                                throw Exception("이미 가입된 이메일입니다.")
                            }

                            email.isBlank() || !email.contains("@") -> {
                                throw Exception("올바른 이메일을 입력해주세요.")
                            }

                            password.length < 6 -> {
                                throw Exception("비밀번호는 6자 이상이어야 합니다.")
                            }

                            nickname.isBlank() -> {
                                throw Exception("닉네임을 입력해주세요.")
                            }

                            else -> {
                                // TODO: 실제로는 dataSource.signUp(request) 호출
                                // val response = dataSource.signUp(request)
                                // if (!response.isSuccessful) throw Exception("회원가입에 실패했습니다.")
                                Unit // Mock 성공
                            }
                        }
                    }.fold(
                        onSuccess = { Result.success(Unit) },
                        onFailure = { Result.failure(it) },
                    )

                emit(result)
            }

        override fun login(
            email: String,
            password: String,
        ): Flow<Result<Unit>> =
            flow {
                val result =
                    runCatching {
                        val request =
                            LoginRequest(
                                email = email,
                                password = password,
                            )

                        val validPassword = mockValidAccounts[email]
                        when {
                            validPassword == null -> {
                                throw Exception("존재하지 않는 계정입니다.")
                            }

                            validPassword != password -> {
                                throw Exception("비밀번호가 올바르지 않습니다.")
                            }

                            else -> {
                                // TODO: 실제로는 dataSource.login(request) 호출
                                // val response = dataSource.login(request)
                                // if (!response.isSuccessful || response.body() == null) {
                                //     throw Exception("로그인에 실패했습니다.")
                                // }
                                // TODO: 토큰을 DataStore에 저장
                                // val tokenInfo = response.body()!!.toTokenInfo()
                                // tokenDataStore.saveTokenInfo(tokenInfo)
                                Unit // Mock 성공
                            }
                        }
                    }.fold(
                        onSuccess = { Result.success(Unit) },
                        onFailure = { Result.failure(it) },
                    )

                emit(result)
            }

        override fun sendVerificationCode(code: String): Flow<Result<Unit>> =
            flow {
                val result =
                    runCatching {
                        if (code == "123456") {
                            Unit // Mock 성공
                        } else {
                            throw Exception("올바르지 않은 인증 코드입니다.")
                        }
                    }.fold(
                        onSuccess = { Result.success(Unit) },
                        onFailure = { Result.failure(it) },
                    )

                emit(result)
            }
    }
