package com.application.animalAlertApp.data.Response.Post

data class GetMyPost(
    val getPost: List<GetPost>,
    val message: String,
    val status: Int
)