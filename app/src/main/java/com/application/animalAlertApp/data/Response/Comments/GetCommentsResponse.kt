package com.application.animalAlertApp.data.Response.Comments

data class GetCommentsResponse(
    val result: List<FindComment>,
    val message: String,
    val status: Int
)