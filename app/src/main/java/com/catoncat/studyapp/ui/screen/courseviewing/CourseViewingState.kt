package com.catoncat.studyapp.ui.screen.courseviewing

import com.catoncat.studyapp.domain.entities.CourseEntity

sealed interface CourseViewingState {
    data class Content(val courseEntity: CourseEntity): CourseViewingState
    data object Loading: CourseViewingState
    data class Error(val reason: String): CourseViewingState
}