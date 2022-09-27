package com.application.animalAlertApp.data.Response.BusinessProfile

import java.io.Serializable

data class Service(
    val __v: Int,
    val _id: String,
    val createdAt: String,
    val price: String,
    val service: String,
    val serviceDescription: String,
    val shopId: String,
    val updatedAt: String,
    val userId: String,
    val pricePeriod:String
):Serializable