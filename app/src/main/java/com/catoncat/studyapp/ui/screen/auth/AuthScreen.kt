package com.catoncat.studyapp.ui.screen.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.catoncat.studyapp.ui.navigation.AppRoute

@Composable
fun AuthScreen(
    viewModel: AuthViewModel = viewModel(),
    backStack: SnapshotStateList<AppRoute>
) {
//    SecureScreen()
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.actionFlow.collect { action ->
            when (action) {
                is AuthAction.OpenScreen -> {
                    backStack.clear()
                    backStack.add(action.route)
                }
            }
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(all = 24.dp),
    ) {
        Box(
            Modifier.weight(1/3.0f).fillMaxWidth(),
        ) {
            Text(
                text = "Вход или регистрация",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Column(
            Modifier.weight(1/3.0f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val currentState = state) {
                is AuthState.Data -> Content(viewModel, currentState)
                is AuthState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(64.dp)
                    )
                }
            }
        }

        Column(
            Modifier.weight(1/3.0f).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

        }

    }
}

@Composable
private fun Content(
    viewModel: AuthViewModel,
    state: AuthState.Data,
) {
    var inputLogin by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    val focusPasswordRequester = remember { FocusRequester() }
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = inputLogin,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = {
                focusPasswordRequester.requestFocus()
            }
        ),
        onValueChange = {
            inputLogin = it
            viewModel.onIntent(AuthIntent.TextInput(inputLogin, inputPassword))
        },
        label = { Text("Почта") }
    )
    Spacer(modifier = Modifier.size(16.dp))
    OutlinedTextField(
        modifier = Modifier
            .focusRequester(focusPasswordRequester)
            .fillMaxWidth(),
        value = inputPassword,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                viewModel.onIntent(AuthIntent.SignIn(inputLogin, inputPassword))
            }
        ),
        onValueChange = {
            inputPassword = it
            viewModel.onIntent(AuthIntent.TextInput(inputLogin, inputPassword))
        },
        label = { Text("Пароль") }
    )
    Spacer(modifier = Modifier.size(16.dp))
    Row(Modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.weight(0.7f),
            onClick = {
                viewModel.onIntent(AuthIntent.SignUp(inputLogin, inputPassword))
            },
            enabled = state.isEnabledSend
        ) {
            Text("Зарегистрироваться")
        }
        Button(
            modifier = Modifier.weight(0.3f),
            onClick = {
                viewModel.onIntent(AuthIntent.SignIn(inputLogin, inputPassword))
            },
            enabled = state.isEnabledSend
        ) {
            Text("Войти")
        }
    }
    if (state.error != null) {
        Text(
            modifier = Modifier,
            text = state.error,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Red,
        )
    }
    if (state.message != null) {
        Text(
            modifier = Modifier,
            text = state.message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Green,
        )
    }
}
//
//@Composable
//fun SecureScreen() {
//    val activity = LocalActivity.current
//    LifecycleStartEffect(Unit) {
//        activity?.window?.setFlags(
//            WindowManager.LayoutParams.FLAG_SECURE,
//            WindowManager.LayoutParams.FLAG_SECURE
//        )
//        onStopOrDispose {
//            activity?.window?.clearFlags(
//                WindowManager.LayoutParams.FLAG_SECURE
//            )
//        }
//    }
//}