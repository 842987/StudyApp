package com.catoncat.studyapp.domain.coursecreating.entities


class LessonEntity (
    val id: Long?,
    val name: String,
    val imageUrl: String,
    val x: Float,
    val y: Float,
    val exercises: List<ExerciseEntity>,
    val requiredLessons: List<RequiredLessonEntity>?
)