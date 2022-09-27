package com.application.animalAlertApp.data.Response.Service

data class ServiceResponse(
    val getService: List<GetService>,
    val message: String,
    val status: Int
)