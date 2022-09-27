package com.application.animalAlertApp.data.Response

data class DeleteAlertsResponse(
    val findAlert: FindAlertX,
    val message: String,
    val status: Int
)