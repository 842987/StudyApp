package com.catoncat.studyapp.domain.auth

import com.catoncat.studyapp.data.AuthRepository

class SignUpUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        login: String,
        password: String,
    ): Result<Unit> {
        return authRepository.singUp(login, password)
    }

}