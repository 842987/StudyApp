package com.catoncat.studyapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.navigation.BottomNavBar
import com.catoncat.studyapp.ui.navigation.NavRoute
import com.catoncat.studyapp.ui.theme.StudyAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            StudyAppTheme {
                val backStack = remember { mutableStateListOf<AppRoute>(AppRoute.AllCourses) }
                backStack.add(AppRoute.CourseCreating)
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = { BottomNavBar(backStack) }) { innerPadding ->
                    NavRoute(Modifier.padding(innerPadding).fillMaxSize(), backStack)
                }


            }
        }
    }
}