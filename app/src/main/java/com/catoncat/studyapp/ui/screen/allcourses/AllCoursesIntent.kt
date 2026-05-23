package com.catoncat.studyapp.ui.screen.allcourses

sealed interface AllCoursesIntent {
    data class Refresh(val query: String): AllCoursesIntent
    data object LoadMore: AllCoursesIntent
    data class TakeCourse(val courseId: Long): AllCoursesIntent
}