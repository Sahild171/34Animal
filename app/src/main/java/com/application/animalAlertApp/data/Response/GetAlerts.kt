package com.application.animalAlertApp.data.Response

data class GetAlerts(
    val findAlert: List<FindAlert>,
    val message: String,
    val status: Int
)