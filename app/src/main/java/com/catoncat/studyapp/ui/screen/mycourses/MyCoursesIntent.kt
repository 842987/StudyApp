package com.catoncat.studyapp.ui.screen.mycourses

import com.catoncat.studyapp.domain.entities.CourseEntity

sealed interface MyCoursesIntent {
    data object Refresh: MyCoursesIntent
    data object LoadMore: MyCoursesIntent
    data class CreateCourse(val name: String, val description: String): MyCoursesIntent
    data class ChooseCourse(val course: CourseEntity): MyCoursesIntent
}