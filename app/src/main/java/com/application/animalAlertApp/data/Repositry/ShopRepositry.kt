package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.BusinessProfile.BusinessProfileResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Service.ServiceResponse
import com.application.animalAlertApp.data.Response.Shop.AddShopResponse
import com.application.animalAlertApp.data.Response.Shop.GetAllShops
import com.application.animalAlertApp.data.Response.ShopStatusResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class ShopRepositry @Inject constructor(val apiService: ApiService) {

    fun addshop(
        businessname: String,
        businessdescription: String,
        mobileno: String,
        location: String
    ): Flow<AddShopResponse> = flow {
        emit(apiService.addShop(
                businessname,
                businessdescription,
                mobileno,
                location)) }.flowOn(Dispatchers.IO)

    fun addshopserivces(
        services: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.addshopservice(services))
    }.flowOn(Dispatchers.IO)


    fun addportfolio(
        uploadLogo: MultipartBody.Part,
        coverPhoto: MultipartBody.Part,
        portfolioLogo: List<MultipartBody.Part>
    ): Flow<GenricResponse> = flow {
        emit(apiService.addShopPic(uploadLogo, coverPhoto, portfolioLogo))
    }.flowOn(Dispatchers.IO)


    fun getAllShops(): Flow<GetAllShops> = flow {
        emit(apiService.getAllshops())
    }.flowOn(Dispatchers.IO)

    fun getserivces(): Flow<ServiceResponse> = flow {
        emit(apiService.getService())
    }.flowOn(Dispatchers.IO)


    fun getbusinessprofile(userId: String): Flow<BusinessProfileResponse> = flow {
        emit(apiService.getShop(userId))
    }.flowOn(Dispatchers.IO)

    fun getstatuscheck(): Flow<ShopStatusResponse> = flow {
        emit(apiService.checkStatus())
    }.flowOn(Dispatchers.IO)


}