package com.catoncat.studyapp.ui.screen.takencourses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseNetworkDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import com.catoncat.studyapp.domain.takencourses.GetTakenCoursesUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class TakenCoursesViewModel : ViewModel() {
    private val mutex = Mutex()
    private val _uiState: MutableStateFlow<TakenCoursesState> =
        MutableStateFlow(TakenCoursesState.Loading)
    val uiState: StateFlow<TakenCoursesState> = _uiState.asStateFlow();
    private val actualResult: MutableList<TakenCoursesState.Item> = mutableListOf()
    private val getTakenCoursesUseCase = GetTakenCoursesUseCase(
        courseRepository = CourseRepository(CourseNetworkDataSource())
    )
    init {
        getData()
    }

    fun onIntent(intent: TakenCoursesIntent) {
        when (intent) {
            is TakenCoursesIntent.LoadMore -> {
                getData(offset = actualResult.size)
            }

            is TakenCoursesIntent.Refresh -> {
                getData(offset = if (actualResult.isEmpty()) 0 else actualResult.size - 1)
            }

            is TakenCoursesIntent.ChooseCourse -> {
                CourseLocalDataSource.currentCourse = intent.course
            }
        }
    }

    fun getData(offset: Int = 0) {
        viewModelScope.launch {
            val isFirstPage = offset == 0
            viewModelScope.launch {
                _uiState.emit(
                    if (isFirstPage) {
                        TakenCoursesState.Loading
                    } else {
                        mutex.withLock {
                            dropLastTemporaryItem()
                            actualResult.add(TakenCoursesState.Item.Loading)
                            (_uiState.value as? TakenCoursesState.Content)?.copy(
                                courses = actualResult.toPersistentList()
                            ) ?: TakenCoursesState.Loading
                        }
                    }
                )

                getTakenCoursesUseCase.invoke(offset).fold(
                    onSuccess = { data ->
                        addItemsToState(isFirstPage, data)
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        _uiState.emit(
                            when (val state = _uiState.value) {
                                is TakenCoursesState.Content -> {
                                    mutex.withLock {
                                        dropLastTemporaryItem()
                                        actualResult.add(TakenCoursesState.Item.Error)
                                        state.copy(
                                            courses = actualResult.toPersistentList()
                                        )
                                    }
                                }

                                is TakenCoursesState.Error,
                                TakenCoursesState.Loading -> {
                                    TakenCoursesState.Error(error.message.orEmpty())
                                }
                            }
                        )
                    }
                )
            }
        }
    }

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
                data.courses.map { item -> TakenCoursesState.Item.Course(item) }
            )
            _uiState.emit(
                TakenCoursesState.Content(
                    isLastPage = data.isLast,
                    courses = actualResult.toPersistentList()
                )
            )
        }
    }

    private fun dropLastTemporaryItem() {
        when (actualResult.last()) {
            is TakenCoursesState.Item.Error,
            is TakenCoursesState.Item.Loading -> actualResult.removeAt(actualResult.lastIndex)

            is TakenCoursesState.Item.Course -> Unit
        }
    }
}