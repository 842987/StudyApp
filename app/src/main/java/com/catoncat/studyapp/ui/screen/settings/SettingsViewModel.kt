package com.catoncat.studyapp.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.AuthRepository
import com.catoncat.studyapp.data.dto.UserDto
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.AuthNetworkDataSource
import com.catoncat.studyapp.data.source.Network
import com.catoncat.studyapp.domain.settings.ChangeProfileSettingsUseCase
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingState
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.Loading);
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow();

    private val changeProfileSettingsUseCase = ChangeProfileSettingsUseCase(
        authRepository = AuthRepository(
            authNetworkDataSource = AuthNetworkDataSource(),
            authLocalDataSource = AuthLocalDataSource
        )
    )

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.LogOut -> {
                AuthLocalDataSource.clearToken()
                AuthLocalDataSource.userDto = null
            }

            is SettingsIntent.ChangeSettings -> {
                viewModelScope.launch {
                    _uiState.emit(SettingsState.Loading)

                    changeProfileSettingsUseCase.invoke(avatarUrl = intent.avatarUrl, username = intent.username)

                    val userDto = AuthLocalDataSource.userDto!!

                    _uiState.emit(
                        SettingsState.Content(
                            userDto.avatarUrl.orEmpty(), userDto.name!!,
                            AuthLocalDataSource.email!!
                        )
                    )
                }
            }
        }
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.emit(SettingsState.Loading)

            val userDto = AuthLocalDataSource.userDto!!

            _uiState.emit(
                SettingsState.Content(
                    userDto.avatarUrl.orEmpty(), userDto.name!!,
                    AuthLocalDataSource.email!!
                )
            )
        }
    }
}