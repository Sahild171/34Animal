package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.AlertDetailResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AlertDetailRepositry @Inject constructor(private val apiService: ApiService){


    fun getAlertDetail(
        alertid: String
    ): Flow<AlertDetailResponse> = flow {
        emit(apiService.getDetailAlert(alertid))
    }.flowOn(Dispatchers.IO)

}