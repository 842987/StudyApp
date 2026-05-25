package com.catoncat.studyapp.domain.coursecreating

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity

class GetCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseEntity: CourseEntity): Result<CourseEntity> {
        return courseRepository.getCourse(courseEntity)
    }
}