package com.application.animalAlertApp.data.Response.Comments

data class GetReplies(
    val findComment: List<FindCommentX>,
    val message: String,
    val status: Int
)