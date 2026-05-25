package com.catoncat.studyapp.domain.auth

class CheckAuthFormatUseCase {
    operator fun invoke(
        login: String,
        password: String
    ): Boolean {
        return login.contains("@") && login.length != login.indexOf("@")+1
    }
}