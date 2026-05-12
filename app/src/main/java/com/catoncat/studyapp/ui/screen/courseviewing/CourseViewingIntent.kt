package com.catoncat.studyapp.ui.screen.courseviewing

sealed interface CourseViewingIntent {
    data object Refresh: CourseViewingIntent
    data class CompleteLesson(val lessonId: Long): CourseViewingIntent
}