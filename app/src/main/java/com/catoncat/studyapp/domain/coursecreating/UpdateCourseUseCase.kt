package com.catoncat.studyapp.domain.coursecreating

import com.catoncat.studyapp.data.CourseRepository
import com.catoncat.studyapp.domain.entities.CourseEntity

class UpdateCourseUseCase(private val courseRepository: CourseRepository) {
    suspend operator fun invoke(courseEntity: CourseEntity) {
        courseRepository.updateCourse(courseEntity)
    }
}
//class GetAllCoursesUseCase(private val courseRepository: CourseRepository) {
//    suspend operator fun invoke(offset: Int): Result<PagingAllCoursesEntity> {
//        return courseRepository.getCourses(page = offset/COUNT, size = COUNT);
//    }

//    private companion object {
//        const val COUNT = 10
//    }
//}