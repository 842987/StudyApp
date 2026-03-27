package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseDto(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("backgroundUrl")
    val backgroundUrl: String?,
    @SerialName("creator")
    val creator: UserDto?,
    @SerialName("lessons")
    val lessons: List<LessonDto>
)