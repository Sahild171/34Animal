package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.Post.GetAllPosts
import com.application.animalAlertApp.data.Response.Post.PostLikeResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class HomeRepositry @Inject
constructor(val apiService: ApiService) {

    fun getAllPosts(page:Int
    ): Flow<GetAllPosts> = flow {
        emit(apiService.getAllPost(page))
    }.flowOn(Dispatchers.IO)


    fun likepost(postId:String): Flow<PostLikeResponse> = flow {
        emit(apiService.likepost(postId))
    }.flowOn(Dispatchers.IO)



}