package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.Auth.SocialLogin
import com.application.animalAlertApp.data.Response.RegisterResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class RegisterUserRepositry
@Inject constructor(private val apiService: ApiService) {

    fun register(
        phoneNo: String,
        email: String,
        name: String,
        location :String,
        password :String,
        dialCode:String,
        lat:String,
        longi:String,
        deviceToken:String
    ): Flow<RegisterResponse> = flow {
        emit(apiService.registerUser(phoneNo,email, name, location, password,dialCode,lat,longi,deviceToken))
    }.flowOn(Dispatchers.IO)


    fun sociallogin(
        email: String,
        name: String,
        deviceToken:String
    ): Flow<SocialLogin> = flow {
        emit(apiService.socailogin(email, name, deviceToken))
    }.flowOn(Dispatchers.IO)









}