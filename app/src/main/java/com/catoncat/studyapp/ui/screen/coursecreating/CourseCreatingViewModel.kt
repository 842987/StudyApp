package com.catoncat.studyapp.ui.screen.coursecreating

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseNetworkDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.domain.coursecreating.GetCourseUseCase
import com.catoncat.studyapp.domain.coursecreating.UpdateCourseUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseCreatingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<CourseCreatingState> =
        MutableStateFlow(CourseCreatingState.Loading);
    val uiState: StateFlow<CourseCreatingState> = _uiState.asStateFlow();

    private val updateCourseUseCase = UpdateCourseUseCase(
        courseRepository = CourseRepository(CourseNetworkDataSource())
    )
    private val getCourseUseCase = GetCourseUseCase(
        courseRepository = CourseRepository(
            CourseNetworkDataSource()
        )
    )

    fun onIntent(intent: CourseCreatingIntent) {
        when (intent) {
            is CourseCreatingIntent.UpdateCourse -> viewModelScope.launch {
                _uiState.emit(CourseCreatingState.Loading)
                updateCourseUseCase.invoke(
                    intent.course
                )
                getData()
                intent.course.lessons.forEach { lessonEntity ->
                    lessonEntity.exercises.forEach { exercise ->
                        Log.d("CourseCreatingViewModel", exercise.name)
                    }
                }
            }

            CourseCreatingIntent.Refresh -> getData()
        }
    }

    init {
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.emit(CourseCreatingState.Loading)

            getCourseUseCase.invoke(CourseLocalDataSource.currentCourse!!)
                .onSuccess { courseEntity ->
                    _uiState.emit(CourseCreatingState.Content(courseEntity))
                }.onFailure { throwable ->
                    _uiState.emit(
                        CourseCreatingState.Error(
                            throwable.message ?: "Ошибка"
                        )
                    )
                }

        }
    }


}