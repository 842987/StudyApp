package com.catoncat.studyapp.ui.screen.courseviewing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.domain.coursecreating.GetCourseUseCase
import com.catoncat.studyapp.domain.coursecreating.UpdateCourseUseCase
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingIntent
import com.catoncat.studyapp.ui.screen.coursecreating.CourseCreatingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseViewingViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<CourseViewingState> =
        MutableStateFlow(CourseViewingState.Loading);
    val uiState: StateFlow<CourseViewingState> = _uiState.asStateFlow();

    //    private val actualResult: MutableList<CourseCreatingState.Lesson> = mutableListOf()
    private val getCourseUseCase = GetCourseUseCase(
        courseRepository = CourseRepository(
            CourseInfoDataSource()
        )
    )

    fun onIntent(intent: CourseViewingIntent) {
        when (intent) {
            CourseViewingIntent.Refresh -> getData()
        }
    }

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.emit(CourseViewingState.Loading)

            getCourseUseCase.invoke(CourseLocalDataSource.currentCourse!!)
                .onSuccess { courseEntity ->
                    _uiState.emit(CourseViewingState.Content(courseEntity))
                }.onFailure { throwable ->
                    _uiState.emit(
                        CourseViewingState.Error(
                            throwable.message ?: "Ошибка"
                        )
                    )
                }
        }
    }
}