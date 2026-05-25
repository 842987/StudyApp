package com.catoncat.studyapp.ui.screen.allcourses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseNetworkDataSource
import com.catoncat.studyapp.domain.allcourses.GetAllCoursesUseCase
import com.catoncat.studyapp.domain.allcourses.TakeCourseUseCase
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class AllCoursesViewModel : ViewModel() {
    private val mutex = Mutex()
    private val _uiState: MutableStateFlow<AllCoursesState> =
        MutableStateFlow(AllCoursesState.Loading)
    val uiState: StateFlow<AllCoursesState> = _uiState.asStateFlow();
    private val actualResult: MutableList<AllCoursesState.Item> = mutableListOf()
    private val getAllCoursesUseCase = GetAllCoursesUseCase(
        courseRepository = CourseRepository(CourseNetworkDataSource())
    )
    private val takeCourseUseCase = TakeCourseUseCase(
        courseRepository = CourseRepository(
            CourseNetworkDataSource()
        )
    )

    fun onIntent(
        intent: AllCoursesIntent
    ) {
        when (intent) {
            is AllCoursesIntent.LoadMore -> {
                getData(offset = actualResult.size)
            }

            is AllCoursesIntent.Refresh -> {
                getData(intent.query/*offset = if (actualResult.isEmpty()) 0 else actualResult.size - 1*/)
            }

            is AllCoursesIntent.TakeCourse -> {
                viewModelScope.launch {
                    takeCourseUseCase.invoke(intent.courseId)
                }
            }
        }
    }

    fun getData(query: String = "", offset: Int = 0) {
        viewModelScope.launch {
            val isFirstPage = offset == 0
            viewModelScope.launch {
                _uiState.emit(
                    if (isFirstPage) {
                        AllCoursesState.Loading
                    } else {
                        mutex.withLock {
                            dropLastTemporaryItem()
                            actualResult.add(AllCoursesState.Item.Loading)
                            (_uiState.value as? AllCoursesState.Content)?.copy(
                                courses = actualResult.toPersistentList()
                            ) ?: AllCoursesState.Loading
                        }
                    }
                )

                getAllCoursesUseCase.invoke(query, offset).fold(
                    onSuccess = { data ->
                        addItemsToState(isFirstPage, data)
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        _uiState.emit(
                            when (val state = _uiState.value) {
                                is AllCoursesState.Content -> {
                                    mutex.withLock {
                                        dropLastTemporaryItem()
                                        actualResult.add(AllCoursesState.Item.Error)
                                        state.copy(
                                            courses = actualResult.toPersistentList()
                                        )
                                    }
                                }

                                is AllCoursesState.Error,
                                AllCoursesState.Loading -> {
                                    AllCoursesState.Error(error.message.orEmpty())
                                }
                            }
                        )
                    }
                )
            }
        }
    }

    private val lastPage = false

    private suspend fun addItemsToState(
        isFirstPage: Boolean,
        data: PagingCoursesEntity,
    ) {
        mutex.withLock {
            if (isFirstPage) {
                actualResult.clear()
            } else {
                dropLastTemporaryItem()
            }
            actualResult.addAll(
                data.courses.map { item -> AllCoursesState.Item.Course(item) }
            )
            _uiState.emit(
                AllCoursesState.Content(
                    isLastPage = data.isLast,
                    courses = actualResult.toPersistentList()
                )
            )
        }
    }

    private fun dropLastTemporaryItem() {
        when (actualResult.last()) {
            is AllCoursesState.Item.Error,
            is AllCoursesState.Item.Loading -> actualResult.removeAt(actualResult.lastIndex)

            is AllCoursesState.Item.Course -> Unit
        }
    }
}