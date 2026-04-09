package com.catoncat.studyapp.ui.screen.takencourses

import com.catoncat.studyapp.domain.entities.CourseEntity
import kotlinx.collections.immutable.PersistentList


sealed interface TakenCoursesState {
    data object Loading : TakenCoursesState
    data class Content(
        val isLastPage: Boolean,
        val courses: PersistentList<Item>
    ) : TakenCoursesState

    data class Error(val reason: String) : TakenCoursesState
    sealed interface Item {
        data object Loading : Item
        data object Error : Item
        data class Course(val entity: CourseEntity) : Item
    }
}