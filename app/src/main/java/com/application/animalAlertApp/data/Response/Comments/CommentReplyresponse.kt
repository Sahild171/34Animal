package com.application.animalAlertApp.data.Response.Comments

data class CommentReplyresponse(
    val message: String,
    val status: Int,
    val replyArray: ReplyComments
)
