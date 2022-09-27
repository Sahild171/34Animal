package com.application.animalAlertApp.data.Response

import java.io.Serializable

data class MessageX(
    val _id: String,
    val addTitle: String,
    val description: String,
    val isActive: Boolean,
    val priority: String,
    val userImage: String,
    val userName: String,
    val userId: String,
    val petName: String,
    val petId: String,
    val createdAt: String,
    val deviceToken: String,
    val petVisibility:Int
) : Serializable