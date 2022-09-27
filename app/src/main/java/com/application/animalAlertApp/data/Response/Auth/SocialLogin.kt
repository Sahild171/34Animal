package com.application.animalAlertApp.data.Response.Auth

data class SocialLogin(
    val message: String,
    val status: Int,
    val token: String,
    val updateToken: FindUser
)