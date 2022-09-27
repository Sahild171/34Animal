package com.application.animalAlertApp.data.Response.BusinessProfile

data class GetShop(
    val _id: String,
    val businessDescription: String,
    val businessName: String,
    val isActive:Boolean,
    val coverPhoto: String,
    val location: String,
    val mobileNo: String,
    val portfolioLogo: List<String>,
    val service: List<Service>,
    val uploadLogo: String,
    val userName: String,
    val userId:String,
    val userEmail:String
)