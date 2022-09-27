package com.application.animalAlertApp.data.Response

data class GetAllAlerts(
    val findAlert: List<MessageX>,
    val message:String,
    val status: Int
)