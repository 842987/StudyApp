package com.catoncat.studyapp.domain.auth

class CheckAuthFormatUseCase {
    operator fun invoke(
        login: String,
        password: String
    ): Boolean {
        return login.contains("@") && login.length != login.indexOf("@")+1
//        return login.length > 2 && login.all { char ->
//            char.isLetterOrDigit() &&
//                    ((char in 'A'..'Z') || (char in 'a'..'z') || char.isDigit())
//        } && password.isNotBlank()
    }
}