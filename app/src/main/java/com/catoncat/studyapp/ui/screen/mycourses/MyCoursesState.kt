package com.catoncat.studyapp.ui.screen.mycourses

import com.catoncat.studyapp.domain.entities.CourseEntity
import kotlinx.collections.immutable.PersistentList


sealed interface MyCoursesState {
    data object Loading : MyCoursesState
    data class Content(
        val isLastPage: Boolean,
        val courses: PersistentList<Item>
    ) : MyCoursesState

    data class Error(val reason: String) : MyCoursesState
    sealed interface Item {
        data object Loading : Item
        data object Error : Item
        data class Course(val entity: CourseEntity) : Item
    }
}