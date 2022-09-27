package com.application.animalAlertApp.data.Response.Request

data class GetRequest(
    val _id: String,
    val recieverId: String,
    val senderId: String,
    val status: String,
    val userImage: String,
    val userName: String,
    val userLocation:String
)