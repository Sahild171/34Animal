package com.application.animalAlertApp.data.Response

data class ResendOtp(
    val message: String,
    val otp: Int,
    val status: Int
)