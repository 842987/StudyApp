package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class AnswerUpdateDto (
    @SerialName("id")
    var id: Long?,
    @SerialName("text")
    val text: String?,
    @SerialName("correct")
    val correct: Boolean,
    @SerialName("exercise_id")
    val exerciseId: Long
)