package com.catoncat.studyapp.data.source

import android.util.Log
import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.PagingAllCoursesDto
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
                Network.supabase.from("users_taken_course").select { count(Count.EXACT)  }
                    .countOrNull()!!

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
            val count = Network.supabase.from("users_taken_course").select { count(Count.EXACT) }
                .countOrNull()!!
            val result =
                PagingAllCoursesDto(
                    last = count == (page * size).toLong(),
                    content = Network.supabase.from("course")
                        .select(columns = Columns.raw("id, name, description, background_url, creator:users(id, username)".trimIndent())) {
                            filter { eq("creator_id", userId) }
                            range(page * (size - 1), page * size - 1)
                            order("id", Order.ASCENDING)
                        }
                        .decodeList<CourseDto>())
            result
        }
    }

    suspend fun getCourse(courseId: Long): Result<CourseDto> = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.supabase.from("course").select {
                filter {
                    eq("id", courseId)
                }
            }.decodeSingle<CourseDto>()
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

    suspend fun createCourse(courseDto: CourseDto) = withContext(Dispatchers.IO) {
        runCatching {
            Network.supabase.from("course").insert(courseDto)
        }
    }

    suspend fun updateCourse(courseDto: CourseDto) = withContext(Dispatchers.IO) {
        runCatching {
//            val result = Network.client.put("${Network.HOST}/api/course/") {
//                setBody(courseDto)
//            }
//            if (result.status != HttpStatusCode.OK) {
//                error("Status: ${result.status}")
//            }
            Network.supabase.from("course").update(courseDto)
        }
    }
}