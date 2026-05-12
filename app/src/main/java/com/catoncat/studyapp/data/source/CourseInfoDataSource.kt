package com.catoncat.studyapp.data.source

import android.util.Log
import com.catoncat.studyapp.data.dto.AnswerUpdateDto
import com.catoncat.studyapp.data.dto.CourseCreateDto
import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.CourseUpdateDto
import com.catoncat.studyapp.data.dto.ExerciseUpdateDto
import com.catoncat.studyapp.data.dto.LessonUpdateDto
import com.catoncat.studyapp.data.dto.PagingAllCoursesDto
import com.catoncat.studyapp.data.dto.RequiredLessonDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.stream.IntStream.range

class CourseInfoDataSource {
    suspend fun getCourses(page: Int, size: Int): Result<PagingAllCoursesDto> = withContext(
        Dispatchers.IO
    ) {
        runCatching {
//            val result = Network.client.get("${Network.HOST}/api/course/") {
//                url {
//                    parameter("pageNumber", page)
//                    parameter("pageSize", size)
//                }
//            }
//            if (result.status != HttpStatusCode.OK) {
//                error("Status: ${result.status}")
//            }
//            result.body()

            val count =
                Network.supabase.from("course").select { count(Count.EXACT) }.countOrNull()!!
            val result = PagingAllCoursesDto(
                last = count == (page * size).toLong(),
                content = Network.supabase.from("course")
                    .select(columns = Columns.raw("id, name, description, background_url, creator:users(id, username)".trimIndent())) {
                        range(
                            page * (size - 1),
                            page * size
                        )
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
//            val result = Network.client.get("${Network.HOST}/api/course/taken/") {
//                url {
//                    parameter("userId", userId)
//                    parameter("pageNumber", page)
//                    parameter("pageSize", size)
//                }
//            }
//            if (result.status != HttpStatusCode.OK) {
//                error("Status: ${result.status}")
//            }
//            result.body()
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
                    range(page * (size - 1), page * size - 1)
                    order("id", Order.ASCENDING)
                }
                .decodeList<CourseResult>()

            Log.d("CourseInfoDataSource", result.toString())

            PagingAllCoursesDto(
                last = count == (page * size).toLong(),
                content = result.map { courseResult ->
                    courseResult.course
                })
//            result
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
//            val result = Network.client.get("${Network.HOST}/api/course/taken/") {
//                url {
//                    parameter("userId", userId)
//                    parameter("pageNumber", page)
//                    parameter("pageSize", size)
//                }
//            }
//            if (result.status != HttpStatusCode.OK) {
//                error("Status: ${result.status}")
//            }
//            result.body()
            val count = Network.supabase.from("course").select {
                filter { eq("creator_id", userId) }
                count(Count.EXACT)
            }.countOrNull() ?: 0

            val content = Network.supabase.from("course")
                .select(columns = Columns.raw("id, name, description, background_url, creator:users(id, username)".trimIndent())) {
                    filter { eq("creator_id", userId) }
                    range(page * (size - 1), page * size - 1)
                    order("id", Order.ASCENDING)
                }
                .decodeList<CourseDto>()

            val result =
                PagingAllCoursesDto(
                    last = count == (page * size).toLong(),
                    content = content
                )

            Log.e("CourseInfoDataSource", content.size.toString())

            result
        }
    }

    suspend fun getCourse(courseId: Long): Result<CourseDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.supabase.from("course")
                .select(Columns.raw("*, " +
                        "creator:users(id, username, avatar_url), " +
                        "lesson(*, exercise(*, answer(*)), users_completed_id:users_completed_lesson(user_id))")) {
                    filter {
                        eq("id", courseId)
                    }
                }.decodeSingle<CourseDto>()
//            , lesson_required_lesson!lesson_id(required_lesson_id) required_lesson:lesson!lesson_id(required_lesson_id,users_completed_id:users(id))
            result.lessons?.forEach { lessonDto ->
                val requiredLessons = Network.supabase.from("lesson_required_lesson")
                    .select(Columns.raw("id:required_lesson_id, users_completed_lesson:lesson!required_lesson_id(users_completed_lesson(user_id))")) {
                    filter{
                        eq("lesson_id", lessonDto.id!!)
                    }
                }.decodeList<RequiredLessonDto>()
                lessonDto.requiredLessons = requiredLessons
            }
//            result.lessons.forEach { lessonDto ->
//                val requiredLesson = Network.supabase.from("lesson_required_lesson")
//            }
            Log.e("CourseInfoDataSource", result.toString())
//            throw RuntimeException("Test")
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

//    suspend fun createCourse(courseDto: CourseDto) = withContext(Dispatchers.IO) {
//        runCatching {
//            val result = Network.client.post("${Network.HOST}/api/course") {
//                body
//                    courseDto
//
//            }
//        }
//    }
//
//    suspend fun createLesson(courseDto: CourseDto) = withContext(Dispatchers.IO) {
//        runCatching {
//            val result = Network.client.post("${Network.HOST}/api/course") {
//                body = courseDto
//            }
//        }
//    }

    suspend fun insertToUsersCompletedLesson(body: Map<String, Long>) {
        Network.supabase.from("users_completed_lesson").insert(body)
    }

    suspend fun createCourse(courseDto: CourseCreateDto) = withContext(Dispatchers.IO) {
        runCatching {
            Log.d("Course creating", Network.supabase.from("course").insert(courseDto).data)
        }
    }

    suspend fun insertToUsersTakenCourse(courseId: Long, userId: Long) =
        withContext(Dispatchers.IO) {
            Network.supabase.from("users_taken_course")
                .insert(mapOf(Pair("course_id", courseId), Pair("user_id", userId)))
        }

    suspend fun updateCourse(
        courseDto: CourseUpdateDto,
        lessonDtoList: List<LessonUpdateDto>,
        exerciseDtoList: List<ExerciseUpdateDto>,
        answerDtoList: List<AnswerUpdateDto>,
        lessonsToDeleteIdList: List<Long>,
        exercisesToDeleteIdList: List<Long>,
        answersDeleteIdList: List<Long>,
    ) = withContext(Dispatchers.IO) {
        runCatching {
//            val result = Network.client.put("${Network.HOST}/api/course/") {
//                setBody(courseDto)
//            }
//            if (result.status != HttpStatusCode.OK) {
//                error("Status: ${result.status}")
//            }
            Log.e("CourseInfoDataSource", "Updating course")

            Network.supabase.from("course").update({
                set("name", courseDto.name)
                set("description", courseDto.description)
                set("background_url", courseDto.backgroundUrl)
            }) {
                filter {
                    eq("id", courseDto.id!!)
                }
            }
            val tag = "CourseInfoDataSource"
            Log.e(tag, lessonDtoList.toString())

//            Network.supabase.from("lesson")
//                .insert(LessonUpdateDto(3, "test", "test", 0.5f, 0.5f, 8))

            Network.supabase.from("lesson").upsert(lessonDtoList) {
                onConflict = "id"
            }
            Network.supabase.from("exercise").upsert(exerciseDtoList) {
                onConflict = "id"
            }
            Network.supabase.from("answer").upsert(answerDtoList) {
                onConflict = "id"
            }

            Network.supabase.from("answer").delete {
                filter {
                    isIn("id", answersDeleteIdList)
                }
            }
            Network.supabase.from("exercise").delete {
                filter {
                    isIn("id", exercisesToDeleteIdList)
                }
            }
            Network.supabase.from("lesson").delete {
                filter {
                    isIn("id", lessonsToDeleteIdList)
                }
            }
        }
    }
}