package com.catoncat.studyapp.domain.takencourses.entities

class CourseEntity (
    val id: Long,
    val name: String,
    val description: String,
    val backgroundUrl: String,
    val creator: UserEntity,
)