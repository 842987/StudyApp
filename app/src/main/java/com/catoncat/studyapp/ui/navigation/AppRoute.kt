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

//    override fun equals(other: Any?): Boolean {
//        return other.toString() == this.toString();
//    }
//
//    override fun toString(): String {
//        return route;
//    }
//
//    override fun hashCode(): Int {
//        return route.hashCode()
//    }
}