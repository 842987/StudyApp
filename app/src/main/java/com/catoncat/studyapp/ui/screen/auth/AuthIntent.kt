package com.catoncat.studyapp.ui.screen.auth

sealed interface AuthIntent {
    data class SignIn(val login: String, val password: String): AuthIntent
    data class SignUp(val login: String, val password: String): AuthIntent
    data class TextInput(val login: String, val password: String): AuthIntent
}