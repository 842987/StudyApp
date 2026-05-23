package com.catoncat.studyapp.ui.screen.settings

import androidx.compose.animation.core.animateValueAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ModifierLocalBeyondBoundsLayout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.ui.navigation.AppRoute

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel<SettingsViewModel>(),
    backStack: SnapshotStateList<AppRoute>, onAvatarChanged: (url: String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getData()
    }
    when (val currentState = state) {
        is SettingsState.Content -> ContentState(currentState, onLogOut = {
            viewModel.onIntent(SettingsIntent.LogOut)
            backStack.clear()
            backStack.add(AppRoute.Auth)
        }, onSave = { avatarUrl, username ->
//            if (avatarUrl != AuthLocalDataSource.userDto?.avatarUrl) {
            onAvatarChanged(avatarUrl)
//            }
            viewModel.onIntent(SettingsIntent.ChangeSettings(avatarUrl, username))
        })

        SettingsState.Loading -> LoadingState()
    }
}

@Composable
fun LoadingState() {
    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(Modifier.align(Alignment.Center))
    }
}

@Composable
fun ContentState(
    content: SettingsState.Content,
    onLogOut: () -> Unit,
    onSave: (avatarUrl: String, username: String) -> Unit,
) {
    val avatarUrl = remember { mutableStateOf(content.avatarUrl) }
    val username = remember { mutableStateOf(content.username) }
//    Box(
//        Modifier
//            .fillMaxSize()
//            .padding(10.dp)
//    ) {
    Column(
        Modifier
            .fillMaxSize()
//                .align(Alignment.TopCenter)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            AsyncImage(
                model = content.avatarUrl,
                contentDescription = "Avatar",
                error = painterResource(com.catoncat.studyapp.R.drawable.baseline_account_circle_24),
                modifier = Modifier.align(Alignment.Center).clip(CircleShape).requiredSize(150.dp)
            )
        }

        Column {
            OutlinedTextField(
                value = avatarUrl.value,
                onValueChange = { value -> avatarUrl.value = value },
                label = { Text("Url на картинку для аватарки") })

            OutlinedTextField(
                value = username.value,
                onValueChange = { value -> username.value = value }, label = { Text("Имя") })

            OutlinedTextField(
                value = content.email,
                onValueChange = {},
                enabled = false,
                label = { Text("Почта") })
        }


        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = {
                onSave(avatarUrl.value, username.value)
            }, Modifier.fillMaxWidth()) {
                Text("Сохранить")
            }

            Button(
                onClick = onLogOut, Modifier
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Выйти из аккаунта")
            }
        }
    }
//    }
}