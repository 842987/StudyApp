package com.catoncat.studyapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform