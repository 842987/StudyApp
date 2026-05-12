package com.catoncat.studyapp.ui.screen.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.catoncat.studyapp.ui.navigation.AppRoute
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesState
import com.catoncat.studyapp.ui.screen.allcourses.AllCoursesViewModel

@Composable
fun SettingsScreen(viewModel: SettingsViewModel = viewModel<SettingsViewModel>(),
                   backStack: SnapshotStateList<AppRoute>) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getData()
    }
    when (val currentState = state) {
        is SettingsState.Content -> ContentState(currentState, onLogOut = {
            viewModel.onIntent(SettingsIntent.LogOut)
            backStack.clear()
            backStack.add(AppRoute.Auth)
        })
        SettingsState.Loading -> LoadingState()
    }
}

@Composable
fun LoadingState() {
    Text("Loading")
}

@Composable
fun ContentState(content: SettingsState.Content, onLogOut: () -> Unit) {
    Box(Modifier.fillMaxSize().padding(10.dp)) {
        Column(Modifier.fillMaxSize().align(Alignment.TopCenter), horizontalAlignment = Alignment.CenterHorizontally) {
                AsyncImage(
                    model = content.avatarUrl,
                    contentDescription = "Avatar",
                    error = painterResource(com.catoncat.studyapp.R.drawable.baseline_account_circle_24))

            Text(content.username)
            Text(content.email)
        }
        Button(onClick = onLogOut, Modifier.fillMaxWidth().align(Alignment.BottomCenter)) {
            Text("Выйти из аккаунта")
        }
    }
}