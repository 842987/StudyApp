package com.catoncat.studyapp.domain.takencourses

import com.catoncat.studyapp.domain.takencourses.entities.PagingTakenCoursesEntity
import com.catoncat.studyapp.data.CourseRepository

class GetTakenCoursesUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(offset: Int): Result<PagingTakenCoursesEntity> {
        return courseRepository.getCoursesUserTook(page = offset/COUNT, size = COUNT);
    }

    private companion object {
        const val COUNT = 10
    }
}