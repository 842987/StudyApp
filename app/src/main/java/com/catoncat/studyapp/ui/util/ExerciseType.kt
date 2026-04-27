package com.catoncat.studyapp.ui.util

enum class ExerciseType(val typeName: String) {
    Choose("Choose"),
    Input("Input");

    override fun toString(): String {
        return name
    }
}