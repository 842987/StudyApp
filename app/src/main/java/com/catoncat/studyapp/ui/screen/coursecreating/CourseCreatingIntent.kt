package com.catoncat.studyapp.ui.screen.coursecreating

import com.catoncat.studyapp.domain.entities.CourseEntity

sealed interface CourseCreatingIntent {
    //    data class CreateLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
//    data class UpdateLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
//    data class UpdateAnswer(val answer: CourseCreatingState.Answer): CourseCreatingIntent
//    data class CreateAnswer(val answer: CourseCreatingState.Answer): CourseCreatingIntent
    data object Refresh: CourseCreatingIntent
    data class UpdateCourse(val course: CourseEntity) : CourseCreatingIntent
}