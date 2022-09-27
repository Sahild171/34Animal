package com.application.animalAlertApp.data.Response

data class NotificationResponse(
    val getNotification: List<GetNotification>,
    val message: String,
    val status: Int
)