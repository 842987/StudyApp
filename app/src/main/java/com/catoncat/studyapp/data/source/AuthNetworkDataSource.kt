package com.catoncat.studyapp.data.source

import android.R
import android.util.Log
import androidx.compose.foundation.layout.Column
import com.catoncat.studyapp.data.dto.UserDto
import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.io.encoding.Base64
import kotlin.uuid.Uuid

class AuthNetworkDataSource {
    suspend fun checkAuth(): Result<Boolean> = withContext(Dispatchers.IO) {
        runCatching {
//            val result = Network.client.get("${Network.HOST}/api/person/login") {
//                addAuthHeader()
//            }
//            result.status == HttpStatusCode.Companion.OK


//            val code =
//                Base64.decode(AuthLocalDataSource.getToken()!!.removePrefix("Basic ").toByteArray())
//                    .toString().split(":")
//            val username = code[0]
//            val password = code[1]
//            val userId = Network.supabase.from("users").select {
//                filter {
//                    eq("username", username)
//                }
//            }.decodeSingleOrNull<String>()

            Network.supabase.auth.signInWith(Email) {
                email = AuthLocalDataSource.email!!
                password = AuthLocalDataSource.password!!
            }

            val userID = Network.supabase.auth.currentUserOrNull()!!.id

            AuthLocalDataSource.userDto =
                Network.supabase.from("users").select(columns = Columns.ALL) {
                    filter {
                        eq("user_id", userID)
                    }
                }.decodeSingleOrNull<UserDto>()

//            Log.e(this.javaClass.name, Network.supabase.from("users").select(columns = Columns.raw("id")) {
//                filter {
//                    eq("user_id", userID)
//                }
//            }.decodeSingleOrNull<String>()?:"Test")

//            AuthLocalDataSource.username =
//                Network.supabase.from("users").select(columns = Columns.raw("username")) {
//                    filter {
//                        eq("user_id", userID)
//                    }
//                }.decodeSingleOrNull<Map<String, String>>()!!["username"]

            true
        }
    }

    suspend fun singUp(
        login: String,
        passwordToSet: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            Network.supabase.auth.signUpWith(Email) {
                email = login
                password = passwordToSet
            }
            Network.supabase.auth.currentUserOrNull()?.id?.let { id ->
                Network.supabase.from("users").insert(
                    mapOf(
                        Pair("user_id", id),
                        Pair("username", "Новый пользователь"),
                        Pair("avatar_url", "")
                    )
                )
            }
            Unit
        }
    }

    //TODO: добавить обработку ошибок
    suspend fun updateUsernameAndAvatarUrl(username: String, avatarUrl: String) =
        withContext(Dispatchers.IO) {
            AuthLocalDataSource.userDto?.let { dto->
                val result =
                    Network.supabase.from("users").update({
                        set("username", username)
                        set("avatar_url", avatarUrl)
                    }) {
                        filter {
                            eq("id", dto.id!!)
                        }
                        select()
                    }.decodeSingle<UserDto>()
                AuthLocalDataSource.userDto = UserDto(dto.id, result.name, result.avatarUrl)
            }
        }
}