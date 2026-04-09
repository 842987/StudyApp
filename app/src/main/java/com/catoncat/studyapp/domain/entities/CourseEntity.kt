package com.catoncat.studyapp.domain.entities


class CourseEntity(
    val id: Long,
    val name: String,
    val description: String,
    val backgroundUrl: String,
    val creator: UserEntity,
    val lessons: List<LessonEntity>
)