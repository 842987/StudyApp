package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RequiredLessonDto(
    @SerialName("id")
    val id: Long?,
//    @SerialName("name")
//    val name: String?
    @SerialName("users_completed_lesson")
    val usersCompletedLesson: Map<String, List<Long>>
)
