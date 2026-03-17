package com.catoncat.studyapp.ui.navigation

import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesScreen
import com.catoncat.studyapp.ui.screen.mycourses.MyCoursesScreen
import com.catoncat.studyapp.ui.screen.settings.SettingsScreen
import com.catoncat.studyapp.ui.screen.takencourses.TakenCoursesScreen

@Composable
fun NavRoute(
    modifier: Modifier,
//    backStack: NavBackStack<NavKey> = rememberNavBackStack  (CoursesList)\
//    backStack: SnapshotStateList<Any> = remember { mutableStateListOf(CoursesList) }
    backStack: SnapshotStateList<AppRoute>
) {

    NavDisplay(
        modifier = modifier,
        backStack = backStack,

        entryProvider = entryProvider {
            entry<AppRoute.AllCourses> {
                AllCoursesScreen(backStack = backStack)
            }
            entry<AppRoute.MyCourses> {
                MyCoursesScreen()
            }
            entry<AppRoute.TakenCourses> {
                TakenCoursesScreen()
            }
            entry<AppRoute.Settings> {
                SettingsScreen()
            }
        }
    )
}

@Composable
fun BottomNavBar(backStack: SnapshotStateList<AppRoute>) {
    val currentRoute = backStack.last();

    NavigationBar {
        NavigationBarItem(
            currentRoute == AppRoute.AllCourses,
            icon = { Text("Все") }, onClick = {
                backStack.add(AppRoute.AllCourses)
            }
        )
        NavigationBarItem(
            currentRoute == AppRoute.MyCourses,
            icon = { Text("Мои") }, onClick = {
                backStack.add(AppRoute.MyCourses)
            }
        )
        NavigationBarItem(
            currentRoute == AppRoute.TakenCourses,
            icon = { Text("Изучаемые") }, onClick = {
                backStack.add(AppRoute.TakenCourses)
            }
        )
        NavigationBarItem(
            currentRoute == AppRoute.Settings,
            icon = { Text("Настройки") }, onClick = {
                backStack.add(AppRoute.Settings)
            }
        )
    }
}