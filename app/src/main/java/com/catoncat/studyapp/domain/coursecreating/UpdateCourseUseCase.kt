package com.catoncat.studyapp.domain.coursecreating

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.CourseEntity

class UpdateCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseEntity: CourseEntity) {
        courseRepository.updateCourse(courseEntity)
    }
}