package com.catoncat.studyapp.ui.navigation

import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Typeface
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import coil3.compose.AsyncImage
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesScreen
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesViewModel
import com.catoncat.studyapp.ui.screen.auth.AuthScreen
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingScreen
import com.catoncat.studyapp.ui.screen.courseviewing.CourseViewingScreen
import com.catoncat.studyapp.ui.screen.mycourses.MyCoursesScreen
import com.catoncat.studyapp.ui.screen.settings.SettingsScreen
import com.catoncat.studyapp.ui.screen.takencourses.TakenCoursesScreen
import com.catoncat.studyapp.ui.theme.Typography

@Composable
fun NavRoute(
    modifier: Modifier,
    backStack: SnapshotStateList<AppRoute>,
    onAvatarChanged: (url: String) -> Unit
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
                SettingsScreen(backStack = backStack, onAvatarChanged = onAvatarChanged)
            }
            entry<AppRoute.CourseCreating> {
                CourseCreatingScreen(backStack = backStack)
            }
            entry<AppRoute.Auth> {
                AuthScreen(backStack = backStack)
            }
            entry<AppRoute.CourseViewing> {
                CourseViewingScreen(backStack = backStack)
            }
        }
    )
}

@Composable
fun BottomNavBar(backStack: SnapshotStateList<AppRoute>, avatarUrl: MutableState<String>) {

    val currentRoute = backStack.last();

    if (currentRoute != AppRoute.CourseCreating && currentRoute != AppRoute.Auth && currentRoute != AppRoute.CourseViewing) {
        ShortNavigationBar {
            ShortNavigationBarItem(
                currentRoute == AppRoute.AllCourses,
                icon = { Text("Все") }, onClick = {
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
                icon = { Text("Изучаемые", textAlign = TextAlign.Center)}, onClick = {
                    backStack.add(AppRoute.TakenCourses)
                }, label = null
            )
            ShortNavigationBarItem(
                currentRoute == AppRoute.Settings,
                icon = {
                    AsyncImage(
                        model = avatarUrl.value,
                        contentDescription = "Avatar",
                        error = painterResource(com.catoncat.studyapp.R.drawable.baseline_account_circle_24),
                        modifier = Modifier
                            .clip(CircleShape)
                            .requiredSize(35.dp)
                    )
                }, onClick = {
                    backStack.add(AppRoute.Settings)
                }, label = null
            )
        }
    }
}