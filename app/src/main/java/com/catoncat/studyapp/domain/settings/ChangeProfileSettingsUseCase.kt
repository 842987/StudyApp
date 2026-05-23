package com.catoncat.studyapp.domain.settings

import com.catoncat.studyapp.data.AuthRepository

class ChangeProfileSettingsUseCase(private val authRepository: AuthRepository) {
    suspend operator fun invoke(
        avatarUrl: String,
        username: String,
    ) {
        authRepository.changeProfileSettings(avatarUrl, username)
    }
}