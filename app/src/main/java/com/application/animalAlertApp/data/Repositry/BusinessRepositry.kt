package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.BusinessProfile.AddPortfolioresponse
import com.application.animalAlertApp.data.Response.BusinessProfile.BusinessProfileResponse
import com.application.animalAlertApp.data.Response.BusinessProfile.EditcoverResopnse
import com.application.animalAlertApp.data.Response.GenricResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class BusinessRepositry @Inject constructor(val apiService: ApiService) {

    fun getbusinessprofile(userId: String): Flow<BusinessProfileResponse> = flow {
        emit(apiService.getShop(userId))
    }.flowOn(Dispatchers.IO)

    fun editcoverphoto(coverPhoto: MultipartBody.Part): Flow<EditcoverResopnse> = flow {
        emit(apiService.editcoverphoto(coverPhoto))
    }.flowOn(Dispatchers.IO)


    fun editshoplogo(uploadLogo: MultipartBody.Part): Flow<GenricResponse> = flow {
        emit(apiService.editshoplogo(uploadLogo))
    }.flowOn(Dispatchers.IO)


    fun editportfolio(portfolioLogo: MultipartBody.Part): Flow<AddPortfolioresponse> = flow {
        emit(apiService.editportfolio(portfolioLogo))
    }.flowOn(Dispatchers.IO)


    fun deleteservice(serviceId: String): Flow<GenricResponse> = flow {
        emit(apiService.deleteService(serviceId))
    }.flowOn(Dispatchers.IO)


    fun deleteportfolio(portfolioLogo: String): Flow<GenricResponse> = flow {
        emit(apiService.deleteportfoliophoto(portfolioLogo))
    }.flowOn(Dispatchers.IO)
}