package com.application.animalAlertApp.data.Response.Comments

data class AddComment(
    val result: FindComment,
    val message: String,
    val status: Int
)