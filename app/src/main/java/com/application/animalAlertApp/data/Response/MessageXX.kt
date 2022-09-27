package com.application.animalAlertApp.data.Response

import java.io.Serializable

data class MessageXX(
    val _id: String,
    val petName: String,
    val petType: String,
    val petColor: String,
    val petBreed: String,
    val userId: String,
    val createdAt: String,
    val description: String,
    val petImages: List<String>,
    val profileImage:String
) : Serializable