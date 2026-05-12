package com.catoncat.studyapp.ui.screen.allcourses

sealed interface AllCoursesIntent {
    data object Refresh: AllCoursesIntent
    data object LoadMore: AllCoursesIntent
    data class TakeCourse(val courseId: Long): AllCoursesIntent
}