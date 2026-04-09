package com.catoncat.studyapp.domain.mycourses

import com.catoncat.studyapp.data.CourseRepository;
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity;


class GetCoursesCreatedByUserUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(offset: Int):Result<PagingCoursesEntity>{
        return courseRepository.getCoursesCreatedByUser(page = offset/COUNT, size = COUNT);
    }

    private companion object {
        const val COUNT = 10
    }

}
