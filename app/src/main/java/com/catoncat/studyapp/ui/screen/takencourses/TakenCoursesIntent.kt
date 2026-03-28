package com.catoncat.studyapp.ui.screen.takencourses

sealed interface TakenCoursesIntent {
    data object Refresh: TakenCoursesIntent
    data object LoadMore: TakenCoursesIntent
}