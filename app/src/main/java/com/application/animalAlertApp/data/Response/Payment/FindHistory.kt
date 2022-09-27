package com.application.animalAlertApp.data.Response.Payment

data class FindHistory(
    val __v: Int,
    val _id: String,
    val amount: String,
    val createdAt: String,
    val expiryDate: String,
    val updatedAt: String,
    val userId: String
)