package com.catoncat.studyapp.data.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.catoncat.studyapp.App
import com.catoncat.studyapp.data.dto.UserDto
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.io.IOException
import kotlin.io.encoding.Base64

object AuthLocalDataSource {
    var email: String? = null
    var password: String? = null

    var userDto: UserDto? = null

    fun setToken(login: String, password: String) {
        this.email = login
        this.password = password
    }

    fun clearToken() {
        this.email = null
        this.password = null
    }

//    private var isInit = false
//    private var _cacheToken: String? = null
//
//    suspend fun getToken(): String? {
//        if (!isInit) {
//            _cacheToken = App.context.dataStore.data.map { preferences ->
//                preferences[TOKEN]
//            }.first()
//            isInit = true
//        }
//        return _cacheToken
//    }
//
//    suspend fun setToken(login: String, password: String) {
//        val decodePhrase = "$login:$password"
//        val token = "Basic ${Base64.encode(decodePhrase.toByteArray())}"
//        _cacheToken = token
//        App.context.dataStore.updateData { prefs ->
//            prefs.toMutablePreferences().also { preferences ->
//                preferences[TOKEN] = token
//            }
//        }
//    }
//
//    fun clearToken() {
//        _cacheToken = null
//        this.email = null
//        this.password = null
//    }
//
//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
//    private val TOKEN = stringPreferencesKey("token")
}