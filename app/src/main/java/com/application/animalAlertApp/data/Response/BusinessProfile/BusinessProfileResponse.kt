package com.application.animalAlertApp.data.Response.BusinessProfile

data class BusinessProfileResponse(
    val getShop: List<GetShop>,
    val message: String,
    val status: Int
)