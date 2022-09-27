package com.application.animalAlertApp.data.Response.Request

data class GetfriendRequests(
    val getRequests: List<GetRequest>,
    val message: String,
    val status: Int,
    val requestlocalstatus:Boolean=false
)