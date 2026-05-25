package com.catoncat.studyapp.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed interface AppRoute/*(val route: String)*/ {
    data object AllCourses : AppRoute/*("Все курсы")*/
    data object MyCourses : AppRoute/*("Мои курсы")*/
    data object Settings : AppRoute/*("Настройки")*/
    data object TakenCourses : AppRoute/*("Проходимые курсы")*/
    data object CourseCreating: AppRoute
    data object Auth: AppRoute
    data object CourseViewing: AppRoute

}