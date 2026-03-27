package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AnswerDto(
    @SerialName("id")
    var id: Long?,
    @SerialName("text")
    val text: String?,
    @SerialName("correct")
    val correct: Boolean?
)