package com.application.animalAlertApp.data.Response.Profile

import com.application.animalAlertApp.data.Response.Auth.FindUser

class EditProfileResponse(
    val status: Int,
    val message: String,
    val data: FindUser
)