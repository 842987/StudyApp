package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
class CourseCreateDto (
    @SerialName("name")
    val name: String?,
    @SerialName("description")
    val description: String?,
    @SerialName("creator_id")
    val creator: Long?,
)