package com.catoncat.studyapp.domain.allcourses

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity

class GetAllCoursesUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(offset: Int): Result<PagingCoursesEntity> {
        return courseRepository.getCourses(page = offset/COUNT, size = COUNT);
    }

    private companion object {
        const val COUNT = 10
    }
}