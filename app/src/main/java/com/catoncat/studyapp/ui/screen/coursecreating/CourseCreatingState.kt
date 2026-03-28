package com.catoncat.studyapp.ui.screen.coursecreating

import kotlinx.collections.immutable.PersistentList

sealed interface CourseCreatingState {
    data class Error(val reason: String) : CourseCreatingState
    data object Loading : CourseCreatingState
    data class Content(
        val course: Course
    ) : CourseCreatingState

    //    sealed interface Lesson {
//        data object Error: Lesson
//        data class Content(val x: Float, val y: Float, val image: String, val name: String, val id: Long): Lesson
//    }
    data class Course (
        val id: Long,
        var name: String,
        var description: String,
        var background: String,
        var lessons: PersistentList<Lesson>,
    )

    data class Lesson(
        var x: Float,
        var y: Float,
        var image: String,
        var name: String,
        val id: Long,
        var exercises: PersistentList<Exercise>
    )

    data class Exercise(
        val id: Long,
        var lessonId: Long?,
        var name: String,
        var text: String,
        var type: String,
        var answers: PersistentList<Answer>
    )

    data class Answer(val id: Long, var text: String, var correct: Boolean)
}