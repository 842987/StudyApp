package com.catoncat.studyapp.ui.screen.settings

sealed interface SettingsState {
    data object Loading: SettingsState
    data class Content(val avatarUrl: String, val username: String, val email: String): SettingsState
}