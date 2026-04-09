package com.catoncat.studyapp.data

import com.catoncat.studyapp.data.dto.AnswerDto
import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.ExerciseDto
import com.catoncat.studyapp.data.dto.LessonDto
import com.catoncat.studyapp.data.dto.RequiredLessonDto
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.data.source.UserLocalDataSource
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import com.catoncat.studyapp.domain.entities.UserEntity

class CourseRepository(
    private val courseInfoDataSource: CourseInfoDataSource,
    private val courseLocalDataSource: CourseLocalDataSource,
    private val userLocalDataSource: UserLocalDataSource
) {
    suspend fun getCourses(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseInfoDataSource.getCourses(page = page, size = size).mapCatching { dto ->
            PagingCoursesEntity(
                isLast = dto.last ?: true,
                courses = dto.content?.mapNotNull { courseDto ->
                    CourseEntity(
                        id = courseDto.id ?: return@mapNotNull null,
                        name = courseDto.name ?: return@mapNotNull null,
                        description = courseDto.description ?: return@mapNotNull null,
                        backgroundUrl = courseDto.backgroundUrl ?: return@mapNotNull null,
                        creator = UserEntity(
                            courseDto.creator?.id ?: return@mapNotNull null,
                            courseDto.creator.name ?: return@mapNotNull null
                        ),
                        lessons = listOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun getCoursesUserTook(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseInfoDataSource.getCoursesUserTook(
            userId = userLocalDataSource.getId(),
            page = page,
            size = size
        ).mapCatching { dto ->
            PagingCoursesEntity(
                isLast = dto.last ?: true,
                courses = dto.content?.mapNotNull { courseDto ->
                    CourseEntity(
                        id = courseDto.id ?: return@mapNotNull null,
                        name = courseDto.name ?: return@mapNotNull null,
                        description = courseDto.description ?: return@mapNotNull null,
                        backgroundUrl = courseDto.backgroundUrl ?: return@mapNotNull null,
                        creator = UserEntity(
                            courseDto.creator?.id ?: return@mapNotNull null,
                            courseDto.creator.name ?: return@mapNotNull null
                        ),
                        lessons = listOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun getCoursesCreatedByUser(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseInfoDataSource.getCoursesCreatedByUser(
            userId = userLocalDataSource.getId(),
            page = page,
            size = size
        ).mapCatching { dto ->
            PagingCoursesEntity(
                isLast = dto.last ?: true,
                courses = dto.content?.mapNotNull { courseDto ->
                    CourseEntity(
                        id = courseDto.id ?: return@mapNotNull null,
                        name = courseDto.name ?: return@mapNotNull null,
                        description = courseDto.description ?: return@mapNotNull null,
                        backgroundUrl = courseDto.backgroundUrl ?: return@mapNotNull null,
                        creator = UserEntity(
                            courseDto.creator?.id ?: return@mapNotNull null,
                            courseDto.creator.name ?: return@mapNotNull null
                        ),
                        lessons = listOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun updateCourse(courseEntity: CourseEntity) {
        courseInfoDataSource.updateCourse(
            CourseDto(
                courseEntity.id,
                courseEntity.name,
                courseEntity.description,
                courseEntity.backgroundUrl,
//                UserDto(courseEntity.creator.id, courseEntity.creator.username),
                null,
                courseEntity.lessons.map { lessonEntity ->
                    LessonDto(
                        lessonEntity.id,
                        lessonEntity.name,
                        lessonEntity.imageUrl,
                        lessonEntity.x,
                        lessonEntity.y,
                        lessonEntity.exercises.map { exerciseEntity ->
                            ExerciseDto(
                                exerciseEntity.id,
                                exerciseEntity.name,
                                exerciseEntity.text,
                                exerciseEntity.typeName,
                                exerciseEntity.answers.map { answerEntity ->
                                    AnswerDto(
                                        answerEntity.id,
                                        answerEntity.text,
                                        answerEntity.correct
                                    )
                                })
                        },
                        lessonEntity.requiredLessons?.map { lessonEntity ->
                            RequiredLessonDto(
                                lessonEntity.id,
                                lessonEntity.name
                            )
                        }
                    )
                }
            )
        )
    }
}