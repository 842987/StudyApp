package com.catoncat.studyapp.ui.screen.coursecreating

import com.catoncat.studyapp.domain.entities.CourseEntity
import kotlinx.collections.immutable.PersistentList

sealed interface CourseCreatingState {
    data class Error(val reason: String) : CourseCreatingState
    data object Loading : CourseCreatingState
    data class Content(
        val course: CourseEntity
    ) : CourseCreatingState
}