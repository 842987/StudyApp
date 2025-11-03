package com.catoncat.studyapp

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "StudyApplication3",
    ) {
        App()
    }
}