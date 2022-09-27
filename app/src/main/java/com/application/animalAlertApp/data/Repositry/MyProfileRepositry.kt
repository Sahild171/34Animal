package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.GetMyPets
import com.application.animalAlertApp.data.Response.Post.GetMyPost
import com.application.animalAlertApp.data.Response.ShopStatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class MyProfileRepositry @Inject constructor(val apiService: ApiService) {

    fun getmyPets(
    ): Flow<GetMyPets> = flow {
        emit(apiService.getPets())
    }.flowOn(Dispatchers.IO)


    fun getmyPost(
    ): Flow<GetMyPost> = flow {
        emit(apiService.getmypost())
    }.flowOn(Dispatchers.IO)


    fun deletepet(
        petId: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.deletepet(petId))
    }.flowOn(Dispatchers.IO)


    fun getstatuscheck(): Flow<ShopStatusResponse> = flow {
        emit(apiService.checkStatus())
    }.flowOn(Dispatchers.IO)



}