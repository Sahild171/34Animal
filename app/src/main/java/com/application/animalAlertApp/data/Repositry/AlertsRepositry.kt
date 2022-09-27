package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.*

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AlertsRepositry @Inject constructor(private val apiService: ApiService) {


    fun alerts(
    ): Flow<GetAllAlerts> = flow {
        emit(apiService.getAlert())
    }.flowOn(Dispatchers.IO)



    fun  getscreen(alertid: String): Flow<GenricResponse> = flow {
        emit(apiService.getscreen(alertid))
    }.flowOn(Dispatchers.IO)






    fun addalert(
        addtitle: String,
        prioroty: String,
        description:String,
        Status:String,
        petId:String?
    ): Flow<AddAlertResponse> = flow {
        emit(apiService.addalert(addtitle,prioroty,description,Status,petId))
    }.flowOn(Dispatchers.IO)




    fun getmyPets(
    ): Flow<GetMyPets> = flow {
        emit(apiService.getPets())
    }.flowOn(Dispatchers.IO)



    fun deletemyAlerts(
        alertid: String,
    ): Flow<DeleteAlertsResponse> = flow {
        emit(apiService.deleteAlert(alertid))
    }.flowOn(Dispatchers.IO)


    fun closealerts(
        alertid: String,
        isActive:Boolean
    ): Flow<DeleteAlertsResponse> = flow {
        emit(apiService.closeAlert(alertid,isActive))
    }.flowOn(Dispatchers.IO)

    fun editalert(
        alertid: String,
        addtitle: String,
        petId:String?,
        priority:String,
        description:String,
    ): Flow<EditAlertResponse> = flow {
        emit(apiService.editAlert(alertid,addtitle,petId
            ,priority,description))
    }.flowOn(Dispatchers.IO)


    fun reportAlert(
        alertId: String,
        reportBy:String,
        reason:String
    ): Flow<GenricResponse> = flow {
        emit(apiService.reportalert(alertId,reportBy, reason))
    }.flowOn(Dispatchers.IO)
}