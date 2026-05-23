package com.catoncat.studyapp.ui.screen.settings

sealed interface SettingsIntent {
    data object LogOut : SettingsIntent

    data class ChangeSettings(val avatarUrl: String, val username: String) : SettingsIntent
}