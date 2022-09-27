package com.application.animalAlertApp.data.Response.Post

data class GetPost(
    val _id: String,
    val createdAt: String,
    val description: String,
    val isActive: Boolean,
    val location: String,
    val postImg: List<String>,
    val userId: String,
    val userName: String,
    val userProfile: String
)