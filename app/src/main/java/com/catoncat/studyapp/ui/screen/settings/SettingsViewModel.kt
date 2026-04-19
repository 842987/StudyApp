package com.catoncat.studyapp.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.dto.UserDto
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingsViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<SettingsState> =
        MutableStateFlow(SettingsState.Loading);
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow();

    fun onIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.LogOut -> {
                AuthLocalDataSource.clearToken()
                AuthLocalDataSource.userDto = null
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