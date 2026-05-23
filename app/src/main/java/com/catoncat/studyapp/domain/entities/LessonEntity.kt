package com.catoncat.studyapp.domain.entities

import kotlinx.collections.immutable.PersistentList


class LessonEntity (
    var id: Long?,
    var name: String,
    var imageUrl: String,
    var imageUrlOnCompleted: String,
    var imageUrlOnLocked: String,
    var x: Float,
    var y: Float,
    var exercises: PersistentList<ExerciseEntity>,
    var requiredLessons: PersistentList<Long>,
    val opened: Boolean = false,
    val completed: Boolean = false,
    var deleted: Boolean = false
)