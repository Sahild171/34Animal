package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Profile.SearchUserResponse
import com.application.animalAlertApp.data.Response.Request.GetfriendRequests
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class FriendsRepositry @Inject constructor(val apiService: ApiService) {


    fun getfirends(
        status: String
    ): Flow<GetfriendRequests> = flow {
        emit(apiService.friends(status))
    }.flowOn(Dispatchers.IO)


    fun removefreinds(
        friendId: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.removeFriend(friendId))
    }.flowOn(Dispatchers.IO)

    fun searchuser(name:String
    ): Flow<SearchUserResponse> = flow {
        emit(apiService.searchUser(name))
    }.flowOn(Dispatchers.IO)
}