package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.NotificationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class NotificationRepositry @Inject constructor(val apiService: ApiService) {

    fun getNotifications(
    ): Flow<NotificationResponse> = flow {
        emit(apiService.getNotification())
    }.flowOn(Dispatchers.IO)

}