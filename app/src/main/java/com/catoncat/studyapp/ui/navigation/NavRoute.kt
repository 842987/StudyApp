package com.catoncat.studyapp.ui.navigation

import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.Typeface
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesScreen
import com.catoncat.studyapp.ui.screen.auth.AuthScreen
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingScreen
import com.catoncat.studyapp.ui.screen.mycourses.MyCoursesScreen
import com.catoncat.studyapp.ui.screen.settings.SettingsScreen
import com.catoncat.studyapp.ui.screen.takencourses.TakenCoursesScreen
import com.catoncat.studyapp.ui.theme.Typography

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
                MyCoursesScreen(backStack = backStack)
            }
            entry<AppRoute.TakenCourses> {
                TakenCoursesScreen(backStack = backStack)
            }
            entry<AppRoute.Settings> {
                SettingsScreen()
            }
            entry<AppRoute.CourseCreating> {
                CourseCreatingScreen(backStack = backStack)
            }
            entry<AppRoute.Auth> {
                AuthScreen(backStack = backStack)
            }
        }
    )
}

@Composable
fun BottomNavBar(backStack: SnapshotStateList<AppRoute>) {

    val currentRoute = backStack.last();


    if (currentRoute != AppRoute.CourseCreating) {
        ShortNavigationBar {
            ShortNavigationBarItem(
                currentRoute == AppRoute.AllCourses,
                icon = { Text("Все", style = Typography.labelSmall) }, onClick = {
                    backStack.add(AppRoute.AllCourses)
                }, label = null
            )
            ShortNavigationBarItem(
                currentRoute == AppRoute.MyCourses,
                icon = { Text("Мои") }, onClick = {
                    backStack.add(AppRoute.MyCourses)
                }, label = null
            )
            ShortNavigationBarItem(
                currentRoute == AppRoute.TakenCourses,
                icon = { Text("Изучаемые", style = Typography.labelSmall) }, onClick = {
                    backStack.add(AppRoute.TakenCourses)
                }, label = null
            )
            ShortNavigationBarItem(
                currentRoute == AppRoute.Settings,
                icon = { Text("Настройки") }, onClick = {
                    backStack.add(AppRoute.Settings)
                }, label = null
            )
        }
    }
}