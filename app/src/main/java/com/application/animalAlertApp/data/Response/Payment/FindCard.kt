package com.application.animalAlertApp.data.Response.Payment

data class FindCard(
    val _id: String,
    val cvc: String,
    val exp_month: String,
    val exp_year: String,
    val number: String,
    val userId: String,
    val cardHolderName:String
)