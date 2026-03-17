package com.catoncat.studyapp.data.source

import com.catoncat.studyapp.data.dto.PagingAllCoursesDto
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CourseInfoDataSource {
    suspend fun getCourses(page: Int, size: Int): Result<PagingAllCoursesDto> = withContext(
Dispatchers.IO
    ) {
        runCatching {
            val result = Network.client.get("${Network.HOST}/api/course/paginated") {
                url {
                    parameter("page", page)
                    parameter("size", size)
                }
            }
            if(result.status != HttpStatusCode.OK) {
                error("Status: ${result.status}")
            }
            result.body()
        }
    }
}