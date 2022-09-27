package com.application.animalAlertApp.data.Response.Payment

data class GetTransactionHistrory(
    val findHistory: List<FindHistory>,
    val message: String,
    val status: Int
)