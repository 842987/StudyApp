package com.catoncat.studyapp.domain.entities

class AnswerEntity(
    var id: Long?,
    var text: String,
    var correct: Boolean,
    var deleted: Boolean = false
)