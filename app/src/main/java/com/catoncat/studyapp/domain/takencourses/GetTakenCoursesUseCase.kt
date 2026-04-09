package com.catoncat.studyapp.domain.takencourses

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity

class GetTakenCoursesUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(offset: Int): Result<PagingCoursesEntity> {
        return courseRepository.getCoursesUserTook(page = offset/COUNT, size = COUNT);
    }

    private companion object {
        const val COUNT = 10
    }
}