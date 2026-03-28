package com.catoncat.studyapp.ui.screen.coursecreating

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.data.source.UserLocalDataSource
import com.catoncat.studyapp.domain.coursecreating.UpdateCourseUseCase
import com.catoncat.studyapp.domain.coursecreating.entities.AnswerEntity
import com.catoncat.studyapp.domain.coursecreating.entities.CourseEntity
import com.catoncat.studyapp.domain.coursecreating.entities.ExerciseEntity
import com.catoncat.studyapp.domain.coursecreating.entities.LessonEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CourseCreatingViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<CourseCreatingState> =
        MutableStateFlow(CourseCreatingState.Loading);
    val uiState: StateFlow<CourseCreatingState> = _uiState.asStateFlow();

    //    private val actualResult: MutableList<CourseCreatingState.Lesson> = mutableListOf()
    private val updateCourseUseCase = UpdateCourseUseCase(
        courseRepository = CourseRepository(CourseInfoDataSource(), CourseLocalDataSource(),
            UserLocalDataSource())
    )

    init {
        getData()
    }

    fun onIntent(intent: CourseCreatingIntent) {
        when (intent) {
            is CourseCreatingIntent.UpdateCourse -> viewModelScope.launch {
                updateCourseUseCase.invoke(
                    CourseEntity(
                        intent.course.id,
                        intent.course.name,
                        intent.course.description,
                        intent.course.background,
                        intent.course.lessons.map { lesson ->
                            LessonEntity(
                                lesson.id,
                                lesson.name,
                                lesson.image,
                                lesson.x,
                                lesson.y,
                                lesson.exercises.map { exercise ->
                                    ExerciseEntity(
                                        exercise.id,
                                        exercise.name,
                                        exercise.text,
                                        exercise.type,
                                        exercise.answers.map { answer ->
                                            AnswerEntity(
                                                answer.id,
                                                answer.text,
                                                answer.correct
                                            )
                                        }
                                    )
                                }, null
                            )
                        }
                    )

                )
            }
        }
    }

    fun getData() {
        viewModelScope.launch {
            _uiState.emit(CourseCreatingState.Loading)

            delay(500)


//            actualResult.addAll(
//                arrayOf(
//                    CourseCreatingState.Lesson(0.0f, 0.0f, "", "Test1", 0, persistentListOf()),
//                    CourseCreatingState.Lesson(50.0f, 50.0f, "", " Test2 ", 0, persistentListOf())
//                )
//            )

            _uiState.emit(
                CourseCreatingState.Content(
                    CourseCreatingState.Course(1, "Test", "Test","Test", persistentListOf())
//                    persistentListOf(CourseCreatingState.Exercise(null, "Test", "Test", "Choose", persistentListOf()))
                )
            )
        }
    }


}