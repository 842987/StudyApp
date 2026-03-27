package com.catoncat.studyapp.data.source

import com.catoncat.studyapp.data.dto.CourseDto
import com.catoncat.studyapp.data.dto.PagingAllCoursesDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseInfoDataSource {
    suspend fun getCourses(page: Int, size: Int): Result<PagingAllCoursesDto> = withContext(
        Dispatchers.IO
    ) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/course/") {
                url {
                    parameter("pageNumber", page)
                    parameter("pageSize", size)
                }
            }
            if (result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
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

    suspend fun updateCourse(courseDto: CourseDto) = withContext(Dispatchers.IO) {
        runCatching {
            val result = Network.client.put("${Network.HOST}/api/course/") {
                setBody(courseDto)
            }
            if(result.status!= HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
        }
    }
}