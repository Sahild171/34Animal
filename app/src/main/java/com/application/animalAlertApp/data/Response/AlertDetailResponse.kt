package com.application.animalAlertApp.data.Response

data class AlertDetailResponse(
    val findAlert: FindAlertXX,
    val message: String,
    val status: Int,
    val petVisibility:Int
)