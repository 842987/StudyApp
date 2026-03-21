package com.catoncat.studyapp.ui.screen.coursecreating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseCreatingViewModel: ViewModel() {
    private val _uiState: MutableStateFlow<CourseCreatingState> = MutableStateFlow(CourseCreatingState.Loading);
    val uiState: StateFlow<CourseCreatingState> = _uiState.asStateFlow();
    private val actualResult: MutableList<CourseCreatingState.Lesson> = mutableListOf()

    init{
        getData()
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.emit(CourseCreatingState.Loading)

            delay(1500)

            actualResult.addAll(arrayOf(
               CourseCreatingState.Lesson(0.0f,0.0f, "", "Test1", 0),
                CourseCreatingState.Lesson(50.0f,50.0f, "", "Test2", 0)
            ))

            _uiState.emit(CourseCreatingState.Content(" ", actualResult.toPersistentList()))
        }
    }


}