package com.catoncat.studyapp.ui.screen.auth

import com.catoncat.studyapp.ui.navigation.AppRoute

sealed interface AuthAction {
    data class OpenScreen(val route: AppRoute): AuthAction
}