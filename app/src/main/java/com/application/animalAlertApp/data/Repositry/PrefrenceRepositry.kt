package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.SavePrefrence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class PrefrenceRepositry @Inject constructor(val apiService:ApiService) {


    fun saveprefrence(
        user_id:String,
        perference:String
    ): Flow<SavePrefrence> = flow {
        emit(apiService.saveprefrence(user_id, perference))
    }.flowOn(Dispatchers.IO)




    fun changePrfrences(
        perference:String
    ): Flow<GenricResponse> = flow {
        emit(apiService.changeprefrence(perference))
    }.flowOn(Dispatchers.IO)







}