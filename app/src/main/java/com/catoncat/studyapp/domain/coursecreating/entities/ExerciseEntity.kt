package com.catoncat.studyapp.domain.coursecreating.entities


class ExerciseEntity(
    val id: Long?,
    val name: String,
    val text: String,
    val typeName: String,
    val answers: List<AnswerEntity>
)