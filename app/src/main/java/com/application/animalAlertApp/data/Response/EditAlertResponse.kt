package com.application.animalAlertApp.data.Response



data class EditAlertResponse(
    val message: String,
    val status: Int,
    val updateAlert: MessageX
)