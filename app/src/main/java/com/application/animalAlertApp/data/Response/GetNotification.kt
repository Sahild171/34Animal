package com.application.animalAlertApp.data.Response

data class GetNotification(
    val _id: String,
    val createdAt: String,
    val notification: String,
    val userId: String,
    val notificationType:String
)