package com.catoncat.studyapp.data

import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.domain.allcourses.entities.CourseEntity
import com.catoncat.studyapp.domain.allcourses.entities.PagingAllCoursesEntity

class CourseRepository(private val courseInfoDataSource: CourseInfoDataSource) {
    suspend fun getCourses(page: Int, size: Int): Result<PagingAllCoursesEntity> {
        return courseInfoDataSource.getCourses(page = page, size = size).mapCatching { dto ->
            PagingAllCoursesEntity(
                isLast = dto.last ?: true,
                courses =  dto.content?.mapNotNull { courseDto ->
                    CourseEntity(
                        name = courseDto.name?: return@mapNotNull null,
                        description = courseDto.description?: return@mapNotNull null
                    )
                } ?: error("Courses list is null")
            )
        }
    }
}