package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ContactRepositry @Inject constructor(val apiService: ApiService) {

    fun contactus(name:String,email:String,message:String): Flow<GenricResponse> = flow {
        emit(apiService.contact(name, email, message))
    }.flowOn(Dispatchers.IO)

}