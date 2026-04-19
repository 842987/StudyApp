package com.catoncat.studyapp.domain.mycourses

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity

class CreateCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(name: String, description:String) {
        courseRepository.createCourse(name, description);
    }
}