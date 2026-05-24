package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LessonDto(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("image_url_on_completed")
    var imageUrlOnCompleted: String,
    @SerialName("image_url_on_locked")
    var imageUrlOnLocked: String,
    @SerialName("x")
    val x: Float?,
    @SerialName("y")
    val y: Float?,
    @SerialName("exercise")
    val exercises: List<ExerciseDto>?,
    @SerialName("required_lesson")
    var requiredLessons: List<Long> = listOf(),
    @SerialName("users_completed_id")
    val usersCompletedId: List<Map<String, Long>>
)
