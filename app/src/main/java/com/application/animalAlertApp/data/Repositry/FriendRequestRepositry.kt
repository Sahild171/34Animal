package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Request.GetfriendRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FriendRequestRepositry @Inject constructor(val apiService: ApiService) {

    fun getrequests(
        status: String
    ): Flow<GetfriendRequests> = flow {
        emit(apiService.getrequest(status))
    }.flowOn(Dispatchers.IO)


    fun acceptorreject(
        status: String,
        freindId: String,
        senderId:String
    ): Flow<GenricResponse> = flow {
        emit(apiService.acceptReject(status,freindId, senderId))
    }.flowOn(Dispatchers.IO)


}