package com.application.animalAlertApp.data.Response.Auth

data class LoginData(
    val findUser: FindUser,
    val message: String,
    val status: Int,
    val token: String
)