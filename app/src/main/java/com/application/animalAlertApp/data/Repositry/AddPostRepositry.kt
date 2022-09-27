package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject



class AddPostRepositry @Inject constructor(val apiService: ApiService){


    fun addPost(
        description: RequestBody,
        location: RequestBody,
        postImg:List<MultipartBody.Part>
    ): Flow<GenricResponse> = flow {
        emit(apiService.addpost(description, location, postImg))
    }.flowOn(Dispatchers.IO)

}