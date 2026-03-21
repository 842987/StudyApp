package com.catoncat.studyapp.ui.screen.coursecreating

sealed interface CourseCreatingIntent {
    data class AddLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
    data class SaveLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
}