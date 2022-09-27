package com.application.animalAlertApp.data.Response.Payment

data class SubscriptionStatus(
    val dates: Dates,
    val message: String,
    val status: Int,
    val currentDate: String,
    val Plan:String

)