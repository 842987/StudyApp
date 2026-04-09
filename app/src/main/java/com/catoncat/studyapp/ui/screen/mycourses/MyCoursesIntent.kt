package com.catoncat.studyapp.ui.screen.mycourses

sealed interface MyCoursesIntent {
    data object Refresh: MyCoursesIntent
    data object LoadMore: MyCoursesIntent
}