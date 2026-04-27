package com.catoncat.studyapp.domain.entities

import kotlinx.collections.immutable.PersistentList


class ExerciseEntity(
    val id: Long?,
    var name: String,
    var text: String,
    var typeName: String,
    var answers: PersistentList<AnswerEntity>,
    var deleted: Boolean = false
)