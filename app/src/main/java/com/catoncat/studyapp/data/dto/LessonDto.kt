package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("x")
    val x: Float?,
    @SerialName("y")
    val y: Float?,
    @SerialName("exercises")
    val exercises: List<ExerciseDto>?,
    @SerialName("requiredLessons")
    val requiredLessons: List<RequiredLessonDto>?
)
