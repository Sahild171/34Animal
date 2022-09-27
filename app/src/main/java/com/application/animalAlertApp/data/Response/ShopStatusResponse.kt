package com.application.animalAlertApp.data.Response


import com.application.animalAlertApp.data.Response.BusinessProfile.GetShop
import com.application.animalAlertApp.data.Response.BusinessProfile.Service



data class ShopStatusResponse(
    val status: Int,
    val message: String,
    val shopStatus: Int,
    val serviceStatus: Int,
    val paymentStatus: Int,
    val logoStatus: String,
    val serviceData: List<Service>,
    val shopData: List<GetShop>
)
