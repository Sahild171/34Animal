package com.application.animalAlertApp.data.Response.Shop

data class GetShop(
    val __v: Int,
    val _id: String,
    val businessDescription: String,
    val businessName: String,
    val coverPhoto: String,
    val createdAt: String,
    val isActive: Boolean,
    val isBuyNow: Boolean,
    val location: String,
    val mobileNo: String,
    val paymentType: String,
    val portfolioLogo: List<String>,
    val price: String,
    val service: String,
    val serviceDescription: String,
    val updatedAt: String,
    val uploadLogo: String,
    val userId: String
)