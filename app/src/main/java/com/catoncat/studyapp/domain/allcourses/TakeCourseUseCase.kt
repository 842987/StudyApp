package com.catoncat.studyapp.domain.allcourses

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity

class TakeCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseId: Long) {
        return courseRepository.addCourseToUserTakenCourses(courseId);
    }
}