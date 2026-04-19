package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CourseUpdateDto (
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("background_url")
    val backgroundUrl: String?,
    @SerialName("creator_id")
    val creatorId: Long?,
)