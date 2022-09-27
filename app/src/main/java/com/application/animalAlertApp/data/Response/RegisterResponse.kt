package com.application.animalAlertApp.data.Response

data class RegisterResponse(
    val message: String,
    val success: Boolean,
    val status:Int,
    val user: User,
    val token: String
)