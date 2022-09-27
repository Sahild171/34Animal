package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.BusinessProfile.EditServiceResponse
import com.application.animalAlertApp.data.Response.Service.ServiceResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class EditServiceRepositry @Inject constructor(val apiService: ApiService) {

    fun getserivces(): Flow<ServiceResponse> = flow {
        emit(apiService.getService())
    }.flowOn(Dispatchers.IO)


    fun editservices(
        serviceId: String,
        service: String,
        priceperiod: String,
        price: String,
        description: String
    ): Flow<EditServiceResponse> = flow {
        emit(apiService.editService(serviceId, service, priceperiod, price, description))
    }.flowOn(Dispatchers.IO)


}