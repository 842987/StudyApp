package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ExerciseDto(
    @SerialName("id")
    val id: Long?,
    @SerialName("name")
    val name: String?,
    @SerialName("text")
    val text: String?,
    @SerialName("typeName")
    val typeName: String?,
    @SerialName("answers")
    val answers: List<AnswerDto>?
)
