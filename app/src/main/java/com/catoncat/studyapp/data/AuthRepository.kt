package com.catoncat.studyapp.data

import com.catoncat.studyapp.data.source.AuthLocalDataSource
import com.catoncat.studyapp.data.source.AuthNetworkDataSource


class AuthRepository(
    private val authNetworkDataSource: AuthNetworkDataSource,
    private val authLocalDataSource: AuthLocalDataSource,
) {
    suspend fun checkAndAuth(
        login: String,
        password: String,
    ): Result<Boolean> {
        authLocalDataSource.setToken(login, password)
        return authNetworkDataSource.checkAuth()
            .onSuccess { isLogin ->
                if (!isLogin) authLocalDataSource.clearToken()
            }
            .onFailure {
                authLocalDataSource.clearToken()
            }
    }
}