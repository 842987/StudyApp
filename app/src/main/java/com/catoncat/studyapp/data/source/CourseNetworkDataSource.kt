package com.catoncat.studyapp.data.source

import android.util.Log
import com.catoncat.studyapp.data.dto.AnswerUpdateDto
import com.catoncat.studyapp.data.dto.CourseCreateDto
import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.CourseUpdateDto
import com.catoncat.studyapp.data.dto.ExerciseUpdateDto
import com.catoncat.studyapp.data.dto.LessonUpdateDto
import com.catoncat.studyapp.data.dto.PagingAllCoursesDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.query.filter.TextSearchType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class CourseNetworkDataSource {
    suspend fun getCourses(query: String, page: Int, size: Int): Result<PagingAllCoursesDto> =
        withContext(
            Dispatchers.IO
        ) {
            runCatching {
                val count =
                    Network.supabase.from("course").select {
                        count(Count.EXACT)
                        if (!query.isEmpty()) {
                            filter {
                                textSearch(
                                    column = "name",
                                    query = query,
                                    textSearchType = TextSearchType.WEBSEARCH
                                )
                            }
                        }
                    }.countOrNull() ?: 0
                val result = PagingAllCoursesDto(
                    last = count <= ((page.toLong()+1) * size),
                    content = Network.supabase.from("course")
                        .select(columns = Columns.raw("id, name, description, background_url, creator:users(id, username)".trimIndent())) {
                            range((size.toLong() + 1) * (page), (page.toLong() + 1) * size)
                            order("id", Order.ASCENDING)
                            if (!query.isEmpty()) {
                                filter {
                                    textSearch(
                                        column = "name",
                                        query = query,
                                        textSearchType = TextSearchType.WEBSEARCH
                                    )
                                }
                            }
                        }
                        .decodeList<CourseDto>())

                result
            }
        }

    suspend fun getCoursesUserTook(
        userId: Long,
        page: Int,
        size: Int
    ): Result<PagingAllCoursesDto> = withContext(
        Dispatchers.IO
    ) {
        runCatching {
            val count =
                Network.supabase.from("users_taken_course").select { count(Count.EXACT) }
                    .countOrNull() ?: 0

            @Serializable
            data class CourseResult(
                @SerialName("course")
                val course: CourseDto
            )


            val result = Network.supabase.from("users_taken_course")
                .select(columns = Columns.raw("course(id, name, description, background_url, creator:users(id, username))".trimIndent())) {
                    filter { eq("user_id", userId) }
                    range((size.toLong() + 1) * (page), (page.toLong() + 1) * size)
                    order("id", Order.ASCENDING)
                }
                .decodeList<CourseResult>()

            Log.d("CourseInfoDataSource", result.toString())

            PagingAllCoursesDto(
                last =  count <= ((page.toLong()+1) * size),
                content = result.map { courseResult ->
                    courseResult.course
                })
        }
    }

    suspend fun getCoursesCreatedByUser(
        userId: Long,
        page: Int,
        size: Int
    ): Result<PagingAllCoursesDto> = withContext(
        Dispatchers.IO
    ) {
        runCatching {
            val count = Network.supabase.from("course").select {
                filter { eq("creator_id", userId) }
                count(Count.EXACT)
            }.countOrNull() ?: 0

            val content = Network.supabase.from("course")
                .select(columns = Columns.raw("id, name, description, background_url, creator:users(id, username)".trimIndent())) {
                    filter { eq("creator_id", userId) }
                    range((size.toLong() + 1) * (page), (page.toLong() + 1) * size)
                    order("id", Order.ASCENDING)
                }
                .decodeList<CourseDto>()

            val result =
                PagingAllCoursesDto(
                    last = count <= ((page.toLong()+1) * size),
                    content = content
                )

            result
        }
    }

    suspend fun getCourse(courseId: Long): Result<CourseDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.supabase.from("course")
                .select(
                    Columns.raw(
                        "*, " +
                                "creator:users(id, username, avatar_url), " +
                                "lesson(*, exercise(*, answer(*)), users_completed_id:users_completed_lesson(user_id))"
                    )
                ) {
                    filter {
                        eq("id", courseId)
                    }
                }.decodeSingle<CourseDto>()
            result.lessons?.forEach { lessonDto ->
                val requiredLessons = Network.supabase.from("lesson_required_lesson")
                    .select(Columns.raw("lesson!required_lesson_id(id)")) {
                        filter {
                            eq("lesson_id", lessonDto.id!!)
                        }
                    }.decodeList<Map<String, Map<String, Long>>>()

                lessonDto.requiredLessons = requiredLessons.map {
                    it["lesson"]!!["id"]!!
                }
            }
            result
        }
    }

    suspend fun addCourseToCoursesUserTook(courseId: Long, userId: Long) =
        withContext(Dispatchers.IO) {
            runCatching {
                @Serializable
                data class Record(
                    @SerialName("user_id")
                    val userId: Long,
                    @SerialName("course_id")
                    val courseId: Long
                )
                Network.supabase.from("users_taken_course").insert(Record(userId, courseId))
            }
        }

    suspend fun addLessonToRequiredLessons(lessonIdsRequiredLessonIds: List<Map<String, Long>>) =
        withContext(
            Dispatchers.IO
        ) {
            Network.supabase.from("lesson_required_lesson").upsert(lessonIdsRequiredLessonIds) {
                onConflict = "lesson_id, required_lesson_id"
            }
        }

    suspend fun insertToUsersCompletedLesson(body: Map<String, Long>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("users_completed_lesson").upsert(body) {
                onConflict = "user_id, lesson_id"
            }
        }

    suspend fun createCourse(courseDto: CourseCreateDto) = withContext(Dispatchers.IO) {
        runCatching {
            Log.d("Course creating", Network.supabase.from("course").insert(courseDto).data)
        }
    }

    suspend fun deleteAllLessonsRequiredLessons(lessonIds: List<Long>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson_required_lesson").delete {
                filter {
                    isIn("lesson_id", lessonIds)
                }
            }
        }

    suspend fun insertToUsersTakenCourse(courseId: Long, userId: Long) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("users_taken_course")
                .upsert(mapOf(Pair("course_id", courseId), Pair("user_id", userId))) {
                    onConflict = "course_id, user_id"
                }
        }

    suspend fun updateLessons(lessonDtoList: List<LessonUpdateDto>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson").upsert(lessonDtoList) {
                onConflict = "id"
            }
        }

    suspend fun addLesson(lessonDto: LessonUpdateDto): LessonUpdateDto =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson").insert(lessonDto) {
                select()
            }.decodeSingle<LessonUpdateDto>()
        }

    suspend fun updateExercises(exerciseDtoList: List<ExerciseUpdateDto>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("exercise").upsert(exerciseDtoList) {
                onConflict = "id"
            }
        }

    suspend fun addExercise(exerciseDto: ExerciseUpdateDto): ExerciseUpdateDto =
        withContext(Dispatchers.IO) {
            Network.supabase.from("exercise").insert(exerciseDto) {
                select()
            }.decodeSingle<ExerciseUpdateDto>()
        }

    suspend fun updateAnswers(answerDtoList: List<AnswerUpdateDto>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("answer").upsert(answerDtoList) {
                onConflict = "id"
            }
        }

    suspend fun addAnswer(answerDto: AnswerUpdateDto): AnswerUpdateDto =
        withContext(Dispatchers.IO) {
            Network.supabase.from("answer").insert(answerDto) {
                select()
            }.decodeSingle<AnswerUpdateDto>()
        }

    suspend fun deleteAnswers(answersDeleteIdList: List<Long>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("answer").delete {
                filter {
                    isIn("id", answersDeleteIdList)
                }
            }
        }

    suspend fun deleteExercises(exercisesToDeleteIdList: List<Long>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("exercise").delete {
                filter {
                    isIn("id", exercisesToDeleteIdList)
                }
            }
        }

    suspend fun deleteLessons(lessonsToDeleteIdList: List<Long>) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson").delete {
                filter {
                    isIn("id", lessonsToDeleteIdList)
                }
            }
        }

    suspend fun updateLesson(lessonDto: LessonUpdateDto) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson").upsert(lessonDto) {
                onConflict = "id"
            }
        }

    suspend fun updateExercise(exerciseDto: ExerciseUpdateDto) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("exercise").upsert(exerciseDto) {
                onConflict = "id"
            }
        }

    suspend fun updateAnswer(answerDto: AnswerUpdateDto) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("answer").upsert(answerDto) {
                onConflict = "id"
            }
        }

    suspend fun deleteExercise(exerciseId: Long) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("exercise").delete {
                filter {
                    eq("id", exerciseId)
                }
            }
        }

    suspend fun deleteAnswer(answerId: Long) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("answer").delete {
                filter {
                    eq("id", answerId)
                }
            }
        }

    suspend fun deleteLesson(lessonId: Long) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("lesson").delete {
                filter {
                    eq("id", lessonId)
                }
            }
        }

    suspend fun updateCourse(
        courseDto: CourseUpdateDto
    ) = withContext(Dispatchers.IO) {
        runCatching {
            Network.supabase.from("course").update({
                set("name", courseDto.name)
                set("description", courseDto.description)
                set("background_url", courseDto.backgroundUrl)
            }) {
                filter {
                    eq("id", courseDto.id!!)
                }
            }
        }
    }
}