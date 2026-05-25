package com.catoncat.studyapp.data

import android.util.Log
import com.catoncat.studyapp.data.dto.AnswerUpdateDto
import com.catoncat.studyapp.data.dto.CourseCreateDto
import com.catoncat.studyapp.data.dto.CourseUpdateDto
import com.catoncat.studyapp.data.dto.ExerciseUpdateDto
import com.catoncat.studyapp.data.dto.LessonUpdateDto
import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.CourseNetworkDataSource
import com.catoncat.studyapp.domain.entities.AnswerEntity
import com.catoncat.studyapp.domain.entities.CourseEntity
import com.catoncat.studyapp.domain.entities.ExerciseEntity
import com.catoncat.studyapp.domain.entities.LessonEntity
import com.catoncat.studyapp.domain.entities.PagingCoursesEntity
import com.catoncat.studyapp.domain.entities.UserEntity
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

class CourseRepository(
    private val courseNetworkDataSource: CourseNetworkDataSource
) {
    suspend fun getCourses(query: String, page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseNetworkDataSource.getCourses(query, page = page, size = size).mapCatching { dto ->
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
        courseNetworkDataSource.insertToUsersCompletedLesson(mapOf(Pair("user_id",
            AuthLocalDataSource.userDto?.id!!), Pair("lesson_id", lessonId)))
    }

    suspend fun getCoursesUserTook(page: Int, size: Int): Result<PagingCoursesEntity> {
        return courseNetworkDataSource.getCoursesUserTook(
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
        return courseNetworkDataSource.getCoursesCreatedByUser(
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
        courseNetworkDataSource.createCourse(
            CourseCreateDto(
                name = name, description = description, creator = AuthLocalDataSource.userDto?.id!!
            )
        )
    }

    suspend fun getCourse(courseEntity: CourseEntity): Result<CourseEntity> {
        return courseNetworkDataSource.getCourse(courseEntity.id).mapCatching { courseDto ->
            val lessons = courseDto.lessons.orEmpty().map { lessonDto ->
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
                    requiredLessons = lessonDto.requiredLessons.toPersistentList(),
                    opened = true,
                    completed = lessonDto.usersCompletedId.contains(mapOf(Pair("user_id",AuthLocalDataSource.userDto?.id!!)))
                )
            }

            lessons.forEach { lessonEntity ->
                lessonEntity.requiredLessons.forEach { requiredLessonId ->
                    var completed = false
                    lessons.forEach {
                        if (it.id==requiredLessonId && it.completed) {
                            completed = true
                            return@forEach
                        }
                    }
                    if (!completed) {
                        lessonEntity.opened = false
                        return@forEach
                    }
                }
            }

            CourseEntity(
                courseDto.id!!,
                courseEntity.name,
                courseDto.description!!,
                courseDto.backgroundUrl!!,
                UserEntity(courseDto.creator?.id!!, courseDto.creator.name!!),
                lessons = lessons.toPersistentList()
            )
        }
    }


    suspend fun addCourseToUserTakenCourses(courseId: Long) {
        courseNetworkDataSource.insertToUsersTakenCourse(courseId, AuthLocalDataSource.userDto?.id!!)
    }

    suspend fun updateCourse(courseEntity: CourseEntity) {
        val lessons = mutableListOf<LessonUpdateDto>()
        val exercises = mutableListOf<ExerciseUpdateDto>()
        val answers = mutableListOf<AnswerUpdateDto>()
        val lessonsToDelete = mutableListOf<Long>()
        val exercisesToDelete = mutableListOf<Long>()
        val answersToDelete = mutableListOf<Long>()
        val lessonIdsRequiredLessonIds = mutableListOf<Map<String, Long>>()

        val tag = "CourseRepository"
        courseEntity.lessons.forEach { lessonEntity ->
            if (!lessonEntity.deleted && lessonEntity.id == null) {
                lessonEntity.id = courseNetworkDataSource.addLesson(LessonUpdateDto(
                    null,
                    lessonEntity.name,
                    lessonEntity.imageUrl,
                    lessonEntity.x,
                    lessonEntity.y,
                    courseId = courseEntity.id
                )).id
                lessonEntity.requiredLessons.forEach { requiredLessonId ->
                    lessonIdsRequiredLessonIds.add(mapOf(Pair("lesson_id", lessonEntity.id!!), Pair("required_lesson_id", requiredLessonId)))
                }
            } else if (lessonEntity.deleted && lessonEntity.id!=null) {
                lessonsToDelete.add(lessonEntity.id!!)
                return@forEach
            } else if (!lessonEntity.deleted){
                val lessonDto = LessonUpdateDto(
                    lessonEntity.id,
                    lessonEntity.name,
                    lessonEntity.imageUrl,
                    lessonEntity.x,
                    lessonEntity.y,
                    courseId = courseEntity.id
                )
                lessons.add(lessonDto)
                lessonEntity.requiredLessons.forEach { requiredLessonId ->
                    lessonIdsRequiredLessonIds.add(mapOf(Pair("lesson_id", lessonEntity.id!!), Pair("required_lesson_id", requiredLessonId)))
                }
            }
            lessonEntity.exercises.forEach { exerciseEntity ->
                lessonEntity.id?:Log.d(tag,"LESSON NULL")
                if(!exerciseEntity.deleted && exerciseEntity.id==null) {
                    exerciseEntity.id = courseNetworkDataSource.addExercise(exerciseDto = ExerciseUpdateDto(
                        null,
                    exerciseEntity.name,
                    exerciseEntity.text,
                    exerciseEntity.typeName,
                    lessonEntity.id!!
                    )).id
                } else if (exerciseEntity.deleted && exerciseEntity.id!=null) {
                    courseNetworkDataSource.deleteExercise(exerciseEntity.id!!)
                    return@forEach
                } else if (!exerciseEntity.deleted){
                    val exerciseDto = ExerciseUpdateDto(
                        exerciseEntity.id,
                        exerciseEntity.name,
                        exerciseEntity.text,
                        exerciseEntity.typeName,
                        lessonEntity.id!!
                    )
                    courseNetworkDataSource.updateExercise(exerciseDto)
                }
                exerciseEntity.answers.forEach { answerEntity ->
                    answerEntity.id?:Log.d(tag,"ANSWER NULL")
                    if (!answerEntity.deleted && answerEntity.id==null) {
                        answerEntity.id = courseNetworkDataSource.addAnswer(AnswerUpdateDto(
                            null,
                            answerEntity.text,
                            answerEntity.correct,
                            exerciseEntity.id!!
                        )).id
                    } else if (answerEntity.deleted && answerEntity.id!=null) {
                        answersToDelete.add(answerEntity.id!!)
                    } else if(!answerEntity.deleted) {
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

        val lessonIds = mutableListOf<Long>()
        lessons.forEach { lesson ->
            lesson.id?.let {
                lessonIds.add(lesson.id)
            }
        }


        courseNetworkDataSource.updateCourse(
            courseDto = CourseUpdateDto(
                courseEntity.id,
                courseEntity.name,
                courseEntity.description,
                courseEntity.backgroundUrl,
                AuthLocalDataSource.userDto?.id!!
            )
        )

        courseNetworkDataSource.deleteAllLessonsRequiredLessons(lessonIds)

        courseNetworkDataSource.addLessonToRequiredLessons(lessonIdsRequiredLessonIds)


        courseNetworkDataSource.updateLessons(lessons)
        courseNetworkDataSource.updateExercises(exercises)
        courseNetworkDataSource.updateAnswers(answers)

        courseNetworkDataSource.deleteAnswers(answersToDelete)
        courseNetworkDataSource.deleteExercises(exercisesToDelete)
        courseNetworkDataSource.deleteLessons(lessonsToDelete)
    }
}