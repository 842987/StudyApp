package com.catoncat.studyapp.data

import androidx.compose.runtime.mutableStateOf
import com.catoncat.studyapp.data.dto.AnswerDto
import com.catoncat.studyapp.data.dto.AnswerUpdateDto
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
import com.catoncat.studyapp.domain.entities.RequiredLessonEntity
import com.catoncat.studyapp.domain.entities.UserEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

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

    suspend fun getCourse(courseEntity: CourseEntity): Result<CourseEntity> {
        return courseInfoDataSource.getCourse(courseEntity.id).mapCatching { courseDto ->
            CourseEntity(
                courseDto.id!!,
                courseEntity.name,
                courseDto.description!!,
                courseDto.backgroundUrl!!,
                UserEntity(courseDto.creator?.id!!, courseDto.creator.name!!),
                courseDto.lessons.orEmpty().map { lessonDto ->
                    LessonEntity(
                        lessonDto.id,
                        lessonDto.name!!,
                        lessonDto.imageUrl!!,
                        lessonDto.x!!,
                        lessonDto.y!!,
                        lessonDto.exercises.orEmpty().map { exerciseDto ->
                            ExerciseEntity(
                                exerciseDto.id,
                                exerciseDto.name!!,
                                exerciseDto.text!!,
                                exerciseDto.typeName!!,
                                exerciseDto.answers.orEmpty().map { answerDto ->
                                    AnswerEntity(
                                        answerDto.id!!,
                                        answerDto.text!!,
                                        answerDto.correct!!
                                    )
                                }.toPersistentList()
                            )
                        }.toPersistentList(),
                        lessonDto.requiredLessons.orEmpty().map { requiredLessonDto ->
                            RequiredLessonEntity(
                                requiredLessonDto.id!!,
                                requiredLessonDto.name!!
                            )
                        }.toPersistentList()
                    )
                }.toPersistentList()
            )
        }
    }

    suspend fun updateCourse(courseEntity: CourseEntity) {
        val lessons = mutableListOf<LessonUpdateDto>()
        val exercises = mutableListOf<ExerciseUpdateDto>()
        val answers = mutableListOf<AnswerUpdateDto>()
        val lessonsToDelete = mutableListOf<Long>()
        val exercisesToDelete = mutableListOf<Long>()
        val answersToDelete = mutableListOf<Long>()

        courseEntity.lessons.forEach { lessonEntity ->
            if (lessonEntity.deleted) {
                lessonsToDelete.add(lessonEntity.id!!)
            } else {
                val lessonDto = LessonUpdateDto(
                    lessonEntity.id,
                    lessonEntity.name,
                    lessonEntity.imageUrl,
                    lessonEntity.x,
                    lessonEntity.y,
                    courseId = courseEntity.id
                )
                lessons.add(lessonDto)
            }
            lessonEntity.exercises.forEach { exerciseEntity ->
                if (exerciseEntity.deleted) {
                    exercisesToDelete.add(exerciseEntity.id!!)
                } else {
                    val exerciseDto = ExerciseUpdateDto(
                        exerciseEntity.id,
                        exerciseEntity.name,
                        exerciseEntity.text,
                        exerciseEntity.typeName,
                        lessonEntity.id!!
                    )
                    exercises.add(exerciseDto)
                }
                exerciseEntity.answers.forEach { answerEntity ->
                    if (answerEntity.deleted) {
                        answersToDelete.add(answerEntity.id!!)
                    } else {
                        val answerDto = AnswerUpdateDto(
                            answerEntity.id,
                            answerEntity.text,
                            answerEntity.correct,
                            exerciseEntity.id!!
                        )
                        answers.add(answerDto)
                    }
                }
            }
        }

        courseInfoDataSource.updateCourse(
            courseDto = CourseUpdateDto(
                courseEntity.id,
                courseEntity.name,
                courseEntity.description,
                courseEntity.backgroundUrl,
                AuthLocalDataSource.userDto?.id!!
            ),
            lessonDtoList = lessons,
            exerciseDtoList = exercises,
            answerDtoList = answers,
            lessonsToDeleteIdList = lessonsToDelete,
            exercisesToDeleteIdList = exercisesToDelete,
            answersDeleteIdList = answersToDelete
        )
    }
}