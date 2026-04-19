package com.catoncat.studyapp.data

import androidx.compose.runtime.mutableStateOf
import com.catoncat.studyapp.data.dto.AnswerDto
import com.catoncat.studyapp.data.dto.CourseCreateDto
import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.CourseUpdateDto
import com.catoncat.studyapp.data.dto.ExerciseDto
import com.catoncat.studyapp.data.dto.ExerciseUpdateDto
import com.catoncat.studyapp.data.dto.LessonDto
import com.catoncat.studyapp.data.dto.LessonUpdateDto
import com.catoncat.studyapp.data.dto.RequiredLessonDto
import com.catoncat.studyapp.data.dto.UserDto
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.data.source.CourseLocalDataSource
import com.catoncat.studyapp.domain.entities.AnswerEntity
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.ExerciseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import com.catoncat.studyapp.domain.entities.UserEntity
import kotlinx.collections.immutable.persistentListOf

class CourseRepository(
    private val courseInfoDataSource: CourseInfoDataSource
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
                        lessons = persistentListOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun getCoursesUserTook(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseInfoDataSource.getCoursesUserTook(
            userId = AuthLocalDataSource.userDto?.id!!,
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
                        lessons = persistentListOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun getCoursesCreatedByUser(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseInfoDataSource.getCoursesCreatedByUser(
            userId = AuthLocalDataSource.userDto?.id!!,
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
                        lessons = persistentListOf()
                    )
                } ?: error("Courses list is null")
            )
        }
    }

    suspend fun createCourse(name: String, description: String) {
        courseInfoDataSource.createCourse(
            CourseCreateDto(
                name = name, description = description, creator = AuthLocalDataSource.userDto?.id!!
            )
        )
    }

    suspend fun updateCourse(courseEntity: CourseEntity) {
        val exercises = mutableListOf<ExerciseEntity>()
        val answers = mutableListOf<AnswerEntity>()
        courseEntity.lessons.forEach { lesson ->
            exercises.addAll(lesson.exercises)
            lesson.exercises.forEach { exercise -> answers.addAll(exercise.answers) }
        }
        courseInfoDataSource.updateCourse(
            courseDto = CourseUpdateDto(
                courseEntity.id,
                courseEntity.name,
                courseEntity.description,
                courseEntity.backgroundUrl,
                AuthLocalDataSource.userDto?.id!!
            ),
            lessonDtoList = courseEntity.lessons.map { lessonEntity ->
                LessonUpdateDto(
                    lessonEntity.id,
                    lessonEntity.name,
                    lessonEntity.imageUrl,
                    lessonEntity.x,
                    lessonEntity.y,
                    courseId = courseEntity.id
                )
            },
            exerciseDtoList = exercises.map { exerciseEntity ->
                ExerciseUpdateDto(
                    exerciseEntity.id,
                    exerciseEntity.name,
                    exerciseEntity.text,
                    exerciseEntity.typeName
                    )
            },
            answerDtoList = answers.map { answerEntity ->
                AnswerDto(
                    answerEntity.id,
                    answerEntity.text,
                    answerEntity.correct
                )
            }
        )
    }
}