package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonUpdateDto(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("x")
    val x: Float?,
    @SerialName("y")
    val y: Float?,
    @SerialName("course_id")
    var courseId: Long?
)
