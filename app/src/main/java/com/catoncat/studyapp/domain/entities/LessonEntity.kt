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
    var opened: Boolean = false,
    val completed: Boolean = false,
    var deleted: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return other is LessonEntity && other.id == id && other.name == name
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + opened.hashCode()
        result = 31 * result + completed.hashCode()
        result = 31 * result + deleted.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + imageUrl.hashCode()
        result = 31 * result + imageUrlOnCompleted.hashCode()
        result = 31 * result + imageUrlOnLocked.hashCode()
        result = 31 * result + exercises.hashCode()
        result = 31 * result + requiredLessons.hashCode()
        return result
    }
}