package com.application.animalAlertApp.data.Response.Shop

data class GetAllShops(
    val countShop: Int,
    val getShop: List<GetShop>,
    val message: String,
    val status: Int,
    val userShop:Int,
    val shopStatus:Int,
    val serviceStatus:Int,
    val paymentStatus:Int,
    val logoStatus:String,
    val subs_expiry_date:String
)