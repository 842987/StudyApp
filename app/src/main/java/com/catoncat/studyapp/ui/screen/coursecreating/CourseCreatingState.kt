package com.catoncat.studyapp.ui.screen.coursecreating

import kotlinx.collections.immutable.PersistentList

sealed interface CourseCreatingState {
    data class Error(val reason: String): CourseCreatingState
    data object Loading: CourseCreatingState
    data class Content(val background: String, val lessons: PersistentList<Lesson>): CourseCreatingState

    sealed interface Lesson {
        data object Error: Lesson
        data class Content(val x: Float, val y: Float, val image: String, val id: Long): Lesson
    }
}