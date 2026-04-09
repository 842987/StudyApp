package com.catoncat.studyapp.domain.entities

data class PagingCoursesEntity(
    val isLast: Boolean,
    val courses: List<CourseEntity>
)