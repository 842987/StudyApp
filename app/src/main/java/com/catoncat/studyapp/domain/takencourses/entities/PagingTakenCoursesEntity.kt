package com.catoncat.studyapp.domain.takencourses.entities

data class PagingTakenCoursesEntity(
    val isLast: Boolean,
    val courses: List<CourseEntity>
)