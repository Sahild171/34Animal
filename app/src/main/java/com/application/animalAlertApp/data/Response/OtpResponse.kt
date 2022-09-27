package com.application.animalAlertApp.data.Response

data class OtpResponse(
    val message: String,
    val success: Boolean,
    val status:Int,
    val updateUser: UpdateUser

)