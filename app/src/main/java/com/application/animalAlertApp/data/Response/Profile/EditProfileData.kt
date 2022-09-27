package com.application.animalAlertApp.data.Response.Profile

data class EditProfileData(
    val email: String,
    val location: String,
    val message: String,
    val name: String,
    val phoneNo: String,
    val status: Int
)