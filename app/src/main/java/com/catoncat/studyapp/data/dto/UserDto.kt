package com.catoncat.studyapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    @SerialName("id")
    val id: Long? = null,
    @SerialName("username")
    val name: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)
