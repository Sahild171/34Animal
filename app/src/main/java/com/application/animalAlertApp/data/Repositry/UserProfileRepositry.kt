package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.OtherUserProfile.GetUserProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class UserProfileRepositry @Inject constructor(val apiService: ApiService) {

    fun getuserprofile(
        userId: String
    ): Flow<GetUserProfile> = flow {
        emit(apiService.getuserprofile(userId))
    }.flowOn(Dispatchers.IO)
}