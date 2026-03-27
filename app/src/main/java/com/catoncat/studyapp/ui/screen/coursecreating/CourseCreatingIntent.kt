package com.catoncat.studyapp.ui.screen.coursecreating

sealed interface CourseCreatingIntent {
    //    data class CreateLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
//    data class UpdateLesson(val lesson: CourseCreatingState.Lesson): CourseCreatingIntent
//    data class UpdateAnswer(val answer: CourseCreatingState.Answer): CourseCreatingIntent
//    data class CreateAnswer(val answer: CourseCreatingState.Answer): CourseCreatingIntent
    data class UpdateCourse(val course: CourseCreatingState.Course) : CourseCreatingIntent
}