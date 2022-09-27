package com.application.animalAlertApp.data.Response.Post


data class FindPost(
    val _id: String,
    val addTitle: String,
    val isActive: Boolean,
    val location: String,
    val postImg: List<String>,
    val userId: String,
    val userName: String,
    val createdAt: String,
    val description: String,
    val userProfile: String,
    var like: Int,
    val likeCount: Int,
    val perference: List<String>
)