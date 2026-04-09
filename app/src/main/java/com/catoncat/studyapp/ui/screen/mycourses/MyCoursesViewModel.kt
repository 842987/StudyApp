package com.catoncat.studyapp.ui.screen.mycourses

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.data.source.UserLocalDataSource
import com.catoncat.studyapp.domain.allcourses.GetAllCoursesUseCase
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import com.catoncat.studyapp.domain.mycourses.GetCoursesCreatedByUserUseCase
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MyCoursesViewModel : ViewModel() {
    private val mutex = Mutex()
    private val _uiState: MutableStateFlow<MyCoursesState> =
        MutableStateFlow(MyCoursesState.Loading)
    val uiState: StateFlow<MyCoursesState> = _uiState.asStateFlow();
    private val actualResult: MutableList<MyCoursesState.Item> = mutableListOf()
    private val getMyCoursesUseCase = GetCoursesCreatedByUserUseCase(
        courseRepository = CourseRepository(
            CourseInfoDataSource(), CourseLocalDataSource(),
            UserLocalDataSource()
        )
    )

    init {
        getData()
    }

    fun onIntent(intent: MyCoursesIntent) {
        when (intent) {
            is MyCoursesIntent.LoadMore -> {
                getData(offset = actualResult.size)
            }

            is MyCoursesIntent.Refresh -> {
                getData(offset = if (actualResult.isEmpty()) 0 else actualResult.size - 1)
            }
        }
    }

    fun getData(offset: Int = 0) {
        viewModelScope.launch {
            val isFirstPage = offset == 0
            viewModelScope.launch {
                // В начале определяем где нарисовать "крутилку"
                _uiState.emit(
                    if (isFirstPage) {
                        MyCoursesState.Loading
                    } else {
                        mutex.withLock {
                            dropLastTemporaryItem()
                            actualResult.add(MyCoursesState.Item.Loading)
                            (_uiState.value as? MyCoursesState.Content)?.copy(
                                courses = actualResult.toPersistentList()
                            ) ?: MyCoursesState.Loading
                        }
                    }
                )

                // Запрашиваем данные
                getMyCoursesUseCase.invoke(offset).fold(
                    onSuccess = { data ->
                        addItemsToState(isFirstPage, data)
                    },
                    onFailure = { error ->
                        error.printStackTrace()
                        _uiState.emit(
                            when (val state = _uiState.value) {
                                is MyCoursesState.Content -> {
                                    mutex.withLock {
                                        dropLastTemporaryItem()
                                        actualResult.add(MyCoursesState.Item.Error)
                                        state.copy(
                                            courses = actualResult.toPersistentList()
                                        )
                                    }
                                }

                                is MyCoursesState.Error,
                                MyCoursesState.Loading -> {
                                    MyCoursesState.Error(error.message.orEmpty())
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
                data.courses.map { item -> MyCoursesState.Item.Course(item) }
            )
            _uiState.emit(
                MyCoursesState.Content(
                    isLastPage = data.isLast,
                    courses = actualResult.toPersistentList()
                )
            )
        }
    }

    private fun dropLastTemporaryItem() {
        when (actualResult.last()) {
            is MyCoursesState.Item.Error,
            is MyCoursesState.Item.Loading -> actualResult.removeAt(actualResult.lastIndex)

            is MyCoursesState.Item.Course -> Unit
        }
    }
}