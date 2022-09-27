package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.BusinessProfile.EditBusinessResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject


class EditBusinessRepositry @Inject constructor(val apiService: ApiService) {

    fun editbusiness(
        businessName: String,
        businessDescription: String,
        mobileNo: String,
        location: String
    ): Flow<EditBusinessResponse> = flow {
        emit(apiService.editShopDetail(businessName, businessDescription, mobileNo, location))
    }.flowOn(Dispatchers.IO)
}