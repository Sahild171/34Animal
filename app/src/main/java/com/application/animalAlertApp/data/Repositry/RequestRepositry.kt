package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RequestRepositry @Inject constructor(val apiService: ApiService) {

    fun sendrequest(
        alertid: String,
        deviceToken:String
    ): Flow<GenricResponse> = flow {
        emit(apiService.sentRequest(alertid,deviceToken))
    }.flowOn(Dispatchers.IO)


}