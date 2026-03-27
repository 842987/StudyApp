package com.catoncat.studyapp.ui.screen.allcourses

import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.domain.allcourses.GetAllCoursesUseCase
import com.catoncat.studyapp.domain.allcourses.entities.CourseEntity
import com.catoncat.studyapp.domain.allcourses.entities.PagingAllCoursesEntity
import com.catoncat.studyapp.domain.coursecreating.GetCourseUseCase
import com.catoncat.studyapp.domain.coursecreating.UpdateCourseUseCase
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.random.Random
import kotlin.random.nextInt

class AllCoursesViewModel : ViewModel() {
    private val mutex = Mutex()
    private val _uiState: MutableStateFlow<AllCoursesState> =
        MutableStateFlow(AllCoursesState.Loading)
    val uiState: StateFlow<AllCoursesState> = _uiState.asStateFlow();
    private val actualResult: MutableList<AllCoursesState.Item> = mutableListOf()
    private val getAllCoursesUseCase = GetAllCoursesUseCase(
        courseRepository = CourseRepository(CourseInfoDataSource())
    )
    init {
        getData()
    }

    fun onIntent(intent: AllCoursesIntent) {
        when (intent) {
            is AllCoursesIntent.LoadMore -> {
                getData(offset = actualResult.size)
            }

            is AllCoursesIntent.Refresh -> {
                getData(offset = if (actualResult.isEmpty()) 0 else actualResult.size - 1)
            }
        }
    }

    fun getData(offset: Int = 0) {
        viewModelScope.launch {
//            _uiState.emit(AllCoursesState.Loading)
//
//            delay(1500)
//
//            for (i in 0..10) {
////                actualResult.add(
//
//
////                        when (Random.nextInt(0, 3)) {
////                            0 -> AllCoursesState.Item.Course(
////                                CourseEntity(
////                                    "Test",
////                                    "Test description"
////                                )
////                            )
////                            1 -> AllCoursesState.Item.Loading
////                            else ->
////                                AllCoursesState.Item.Error
////                        }
////                    AllCoursesState.Item.Course(
////                        CourseEntity(
////                            "Test",
////                            "Test description"
////                        )
////                    )
////                )
//
//
//            }
//
//            _uiState.emit(AllCoursesState.Content(true, actualResult.toPersistentList()))
            val isFirstPage = offset == 0
            viewModelScope.launch {
                // В начале определяем где нарисовать "крутилку"
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

                // Запрашиваем данные
                getAllCoursesUseCase.invoke(offset).fold(
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

    private suspend fun addItemsToState(
        isFirstPage: Boolean,
        data: PagingAllCoursesEntity,
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