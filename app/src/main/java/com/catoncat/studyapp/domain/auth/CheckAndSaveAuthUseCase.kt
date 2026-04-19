package com.catoncat.studyapp.domain.auth

import com.catoncat.studyapp.data.AuthRepository

class CheckAndSaveAuthUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        login: String,
        password: String,
    ): Result<Unit> {
        return authRepository.checkAndAuth(login, password).mapCatching { isLogin ->
            if (!isLogin) error("Почта или пароль введены неверно")
        }
    }
}