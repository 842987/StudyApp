package com.catoncat.studyapp.ui.screen.allcourses

sealed interface AllCoursesIntent {
    data object Refresh: AllCoursesIntent
    data object LoadMore: AllCoursesIntent
}