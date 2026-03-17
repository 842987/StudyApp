package com.catoncat.studyapp.ui.screen.allcourses

import com.catoncat.studyapp.domain.allcourses.entities.CourseEntity
import kotlinx.collections.immutable.PersistentList


sealed interface AllCoursesState {
    data object Loading : AllCoursesState
    data class Content(
        val isLastPage: Boolean,
        val courses: PersistentList<Item>
    ) : AllCoursesState

    data class Error(val reason: String) : AllCoursesState
    sealed interface Item {
        data object Loading : Item
        data object Error : Item
        data class Course(val entity: CourseEntity) : Item
    }
}