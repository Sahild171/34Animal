package com.application.animalAlertApp.data.Response.Payment

data class GetCardResponse(
    val findCard: List<FindCard>,
    val message: String,
    val status: Int
)