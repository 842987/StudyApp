package com.catoncat.studyapp.ui.screen.settings

sealed interface SettingsIntent {
    data object LogOut: SettingsIntent
}