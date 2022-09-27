package com.application.animalAlertApp.data.Response.OtherUserProfile

data class UserProfile(
    val _id: String,
    val dialCode: String,
    val image: String,
    val location: String,
    val name: String,
    val phoneNo: String,
    val lat:String,
    val long:String,
    val perference:List<String>,
    val locationVisibility:Boolean
)