package com.catoncat.studyapp.domain.courseviewing

import com.catoncat.studyapp.data.CourseRepository

class CompleteLessonUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(lessonId: Long) {
//        courseRepository.updateCourse(courseEntity)
        return courseRepository.addLessonToCompletedLessons(lessonId)
    }
}