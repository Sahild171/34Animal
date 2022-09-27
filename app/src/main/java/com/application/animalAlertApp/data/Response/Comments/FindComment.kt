package com.application.animalAlertApp.data.Response.Comments


data class FindComment(
    val _id: String,
    val comment: String,
    val postDate: String,
    val postId: String,
    val postTitle: String,
    val userName1: String,
    val image:String,
    val like:Int,
    val likeCount:Int,
    val replyArray:ArrayList<ReplyComments>,
    var visibilitystatus:Boolean

)