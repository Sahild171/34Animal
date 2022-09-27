package com.application.animalAlertApp.data.Response.Post

data class GetAllPosts(
    val findPost: List<FindPost>,
    val message: String,
    val status: Int,
    val countPost:Int
)