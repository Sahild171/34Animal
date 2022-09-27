package com.application.animalAlertApp.data.Response.Shop

data class AddShopResponse(
    val businessName: String,
    val docs: Docs,
    val message: String,
    val status: Int,
    val userId: String
)