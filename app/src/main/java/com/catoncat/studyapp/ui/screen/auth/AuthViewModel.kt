package com.catoncat.studyapp.ui.screen.auth

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.AuthRepository
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.AuthNetworkDataSource
import com.catoncat.studyapp.domain.auth.CheckAndSaveAuthUseCase
import com.catoncat.studyapp.domain.auth.CheckAuthFormatUseCase
import com.catoncat.studyapp.domain.auth.SignUpUseCase
import com.catoncat.studyapp.ui.navigation.AppRoute
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val checkAuthFormatUseCase by lazy { CheckAuthFormatUseCase() }
    private val checkAndSaveAuthCodeUseCase by lazy {
        CheckAndSaveAuthUseCase(
            AuthRepository(
                authNetworkDataSource = AuthNetworkDataSource(),
                authLocalDataSource = AuthLocalDataSource
            )
        )
    }
    private val signUpUseCase by lazy {
        SignUpUseCase(
            AuthRepository(
                authNetworkDataSource = AuthNetworkDataSource(),
                authLocalDataSource = AuthLocalDataSource
            )
        )
    }
    private val _uiState = MutableStateFlow<AuthState>(
        AuthState.Data(
            isEnabledSend = false,
            error = null,
            message = null
        )
    )
    val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _actionFlow = MutableSharedFlow<AuthAction>()

    val actionFlow = _actionFlow.asSharedFlow()

    fun onIntent(intent: AuthIntent) {
        when (intent) {
            is AuthIntent.SignIn -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    _uiState.emit(AuthState.Loading)

                    checkAndSaveAuthCodeUseCase.invoke(intent.login, intent.password).fold(
                        onSuccess = {
                            _actionFlow.emit(
                                AuthAction.OpenScreen(AppRoute.AllCourses)
                            )
                        },
                        onFailure = { error ->
                            _uiState.emit(state)
                            updateStateIfData { oldState ->
                                oldState.copy(
                                    error = error.message
                                )
                            }
                        }
                    )
                }
            }

            is AuthIntent.SignUp -> {
                viewModelScope.launch {
                    val state = _uiState.value
                    _uiState.emit(AuthState.Loading)

                    signUpUseCase.invoke(intent.login, intent.password).fold(
                        onSuccess = {
                            _uiState.emit(state)
                            updateStateIfData { oldState ->
                                oldState.copy(
                                    message = "Аккаунт зарегистрирован. Теперь войдите"
                                )
                            }
                        },
                        onFailure = { error ->
                            _uiState.emit(state)
                            updateStateIfData { oldState ->
                                oldState.copy(
                                    error = error.message
                                )
                            }
                        }
                    )
                }
            }

            is AuthIntent.TextInput -> {
                updateStateIfData { oldState ->
                    oldState.copy(
                        isEnabledSend = checkAuthFormatUseCase.invoke(
                            intent.login,
                            intent.password
                        ),
                        error = null
                    )
                }
            }
        }
    }

    private fun updateStateIfData(lambda: (AuthState.Data) -> AuthState) {
        _uiState.update { state ->
            (state as? AuthState.Data)?.let { lambda.invoke(it) } ?: state
        }

    }
}