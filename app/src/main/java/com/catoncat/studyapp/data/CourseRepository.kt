package com.catoncat.studyapp.data

import android.util.Log
import com.catoncat.studyapp.data.dto.AnswerUpdateDto
import com.catoncat.studyapp.data.dto.CourseCreateDto
import com.catoncat.studyapp.data.dto.CourseUpdateDto
import com.catoncat.studyapp.data.dto.ExerciseUpdateDto
import com.catoncat.studyapp.data.dto.LessonUpdateDto
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.CourseInfoDataSource
import com.catoncat.studyapp.domain.entities.AnswerEntity
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.ExerciseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
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

    suspend fun addLessonToCompletedLessons(lessonId: Long) {
        courseInfoDataSource.insertToUsersCompletedLesson(mapOf(Pair("user_id",
            AuthLocalDataSource.userDto?.id!!), Pair("lesson_id", lessonId)))
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
                    var opened = true
                    val requiredLessonsId =
                        lessonDto.requiredLessons.orEmpty().map { requiredLessonDto ->
//                                RequiredLessonEntity(
//                                   requiredLessonDto.id!!,
//                                    requiredLessonDto.name!!
//                                )
                            if (!requiredLessonDto.usersCompletedLesson["users_completed_lesson"]!!.contains(AuthLocalDataSource.userDto?.id)) {
                                opened = false
                            }

                            Log.d("CourseRepository", lessonDto.id.toString())
                            Log.d("CourseRepository", requiredLessonDto.usersCompletedLesson["users_completed_lesson"]!!.size.toString())
                            Log.d("CourseRepository", requiredLessonDto.usersCompletedLesson["users_completed_lesson"]!!.contains(AuthLocalDataSource.userDto?.id).toString())
                            Log.d("CourseRepository", opened.toString())
                            requiredLessonDto.id!!
                        }.toPersistentList()
                    LessonEntity(
                        lessonDto.id,
                        lessonDto.name!!,
                        lessonDto.imageUrl!!,
                        lessonDto.imageUrlOnCompleted,
                        lessonDto.imageUrlOnLocked,
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
                        requiredLessons = requiredLessonsId,
                        opened = opened,
                        completed = lessonDto.usersCompletedId.contains(AuthLocalDataSource.userDto?.id!!)
                    )
                }.toPersistentList()
            )
        }
    }


    suspend fun addCourseToUserTakenCourses(courseId: Long) {
        courseInfoDataSource.insertToUsersTakenCourse(courseId, AuthLocalDataSource.userDto?.id!!)
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