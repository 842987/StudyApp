package com.catoncat.studyapp.data.source

import android.net.http.HttpEngine
import android.util.Log
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.logging.LogLevel
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.supabaseJson
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.SetupRequest.install
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Network {
    @OptIn(SupabaseInternal::class)
    val supabase = createSupabaseClient(
        supabaseUrl = "https://drayamkugnignib.beget.app",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYW5vbiIsImlzcyI6InN1cGFiYXNlIiwiaWF0IjoxNzc5NDA4MDAwLCJleHAiOjE5MzcxNzQ0MDB9.zOt0hMIi2HyVSuoEqv95dHoTsU0xYBY-yv4X0bCDLv8"
    ) {
        install(Postgrest)

        install(Auth)
        defaultLogLevel = LogLevel.INFO
        httpConfig {
            install(Logging) {
                level = io.ktor.client.plugins.logging.LogLevel.ALL
                logger = Logger.ANDROID
            }
        }
    }
}