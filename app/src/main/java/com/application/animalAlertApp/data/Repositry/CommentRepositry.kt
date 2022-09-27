package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.Comments.AddComment
import com.application.animalAlertApp.data.Response.Comments.CommentReplyresponse
import com.application.animalAlertApp.data.Response.Comments.GetCommentsResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class CommentRepositry @Inject constructor(val apiService: ApiService){

    fun getcomments(postid:String): Flow<GetCommentsResponse> = flow {
        emit(apiService.getComment(postid))
    }.flowOn(Dispatchers.IO)


    fun addcomment(postid:String,comment:String): Flow<AddComment> = flow {
        emit(apiService.addcomment(postid,comment))
    }.flowOn(Dispatchers.IO)


    fun replycomment(commentId:String,reply:String): Flow<CommentReplyresponse> = flow {
        emit(apiService.replyComment(commentId,reply))
    }.flowOn(Dispatchers.IO)


    fun likecomment(commentId:String): Flow<GenricResponse> = flow {
        emit(apiService.likecomment(commentId))
    }.flowOn(Dispatchers.IO)

}