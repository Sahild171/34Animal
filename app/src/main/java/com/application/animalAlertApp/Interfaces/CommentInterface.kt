package com.application.animalAlertApp.Interfaces

interface CommentInterface {
    fun onLikeComment(commentId:String)
    fun onReplyComment(commentId:String,name:String,pos:Int)
}