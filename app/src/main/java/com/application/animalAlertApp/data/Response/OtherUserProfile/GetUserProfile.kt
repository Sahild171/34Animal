package com.application.animalAlertApp.data.Response.OtherUserProfile

import com.application.animalAlertApp.data.Response.MessageXX

data class GetUserProfile(
    val message: String,
    val status: Int,
    val userProfile: List<UserProfile>,
    val friendRequest:String,
    val lat:String,
    val long:String,
    val findPet: List<MessageXX>
)