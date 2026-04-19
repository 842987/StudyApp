package com.catoncat.studyapp.data.source

import android.net.http.HttpEngine
import android.util.Log
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.PropertyConversionMethod
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.api.SetupRequest.install
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

object Network {
    //    const val HOST = "http://10.0.2.2:8080"
//    const val HOST = "http://192.168.1.56:8080"

    //    val client by lazy {
//        HttpClient(CIO) {
//            install(ContentNegotiation) {
//                json(
//                    Json {
//                        isLenient = true
//                        ignoreUnknownKeys = true
//                    }
//                )
//            }
//            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
//                        Log.d("KTOR", message)
//                    }
//                }
//            }
//
//            defaultRequest {
//                contentType(ContentType.Application.Json)
//            }
//        }
//    }
    val supabase = createSupabaseClient(
        supabaseUrl = "https://yjhpwtkydrepghwxzwlw.supabase.co",
        supabaseKey = "sb_publishable_tiyHL9LCY3C6TzmsvWyGfA_NGVvngS6"
    ) {
        install(Postgrest) {
//            defaultSchema = "schema" // default: "public"
//            propertyConversionMethod =
//                PropertyConversionMethod.SERIAL_NAME // default: PropertyConversionMethod.CAMEL_CASE_TO_SNAKE_CASE


        }
        install(Auth)
    }
}