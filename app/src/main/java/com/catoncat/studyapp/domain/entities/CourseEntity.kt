package com.catoncat.studyapp.domain.entities

import kotlinx.collections.immutable.PersistentList


class CourseEntity(
    val id: Long,
    var name: String,
    var description: String,
    var backgroundUrl: String,
    val creator: UserEntity,
    var lessons: PersistentList<LessonEntity>
)