package com.catoncat.studyapp.data.source

import io.github.jan.supabase.auth.OtpType
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
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
            val code =
                Base64.decode(AuthLocalDataSource.getToken()!!.removePrefix("Basic ").toByteArray())
                    .toString().split(":")
            val username = code[0]
            val password = code[1]
            val userId = Network.supabase.from("users").select {
                filter {
                    eq("username", username)
                }
            }.decodeSingleOrNull<String>()


            if (userId != null) {
               true
            } else false
        }
    }
}