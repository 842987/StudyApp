package com.catoncat.studyapp.ui.screen.takencourses

import com.catoncat.studyapp.domain.entities.CourseEntity

sealed interface TakenCoursesIntent {
    data object Refresh: TakenCoursesIntent
    data object LoadMore: TakenCoursesIntent
    data class ChooseCourse(val course: CourseEntity): TakenCoursesIntent
}