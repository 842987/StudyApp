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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import com.catoncat.studyapp.data.source.AuthLocalDataSource
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
                val backStack = rememberSaveable { mutableStateListOf<AppRoute>(AppRoute.Auth) }
                val avatarUrl =
                    remember { mutableStateOf(AuthLocalDataSource.userDto?.avatarUrl ?: "") }
                if (backStack[backStack.size-1] != AppRoute.Auth) {
                    avatarUrl.value = AuthLocalDataSource.userDto?.avatarUrl ?: ""
                }
                Scaffold(modifier = Modifier.fillMaxSize(), bottomBar = {
                    BottomNavBar(backStack, avatarUrl)
                }) { innerPadding ->
                    NavRoute(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(), backStack, onAvatarChanged = { url ->
                            avatarUrl.value = url
                        })
                }
            }
        }
    }
}