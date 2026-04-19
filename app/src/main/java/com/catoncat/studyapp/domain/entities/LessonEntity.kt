package com.catoncat.studyapp.domain.entities

import kotlinx.collections.immutable.PersistentList


class LessonEntity (
    val id: Long?,
    var name: String,
    val imageUrl: String,
    var x: Float,
    var y: Float,
    var exercises: PersistentList<ExerciseEntity>,
    val requiredLessons: PersistentList<RequiredLessonEntity>?
)