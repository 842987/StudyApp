package com.catoncat.studyapp.domain.allcourses.entities

data class PagingAllCoursesEntity(
    val isLast: Boolean,
    val courses: List<CourseEntity>
)