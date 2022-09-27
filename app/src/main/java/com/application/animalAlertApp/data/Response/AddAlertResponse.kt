package com.application.animalAlertApp.data.Response

data class AddAlertResponse(
    val docs: DocsXX,
    val message: String,
    val status: Int,
    val userId: String
)