package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagingAllCoursesDto(
    @SerialName("last")
    val last: Boolean? = null,
    @SerialName("content")
    val content: List<CourseDto>? = null
)