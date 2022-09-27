package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.Auth.LoginData
import com.application.animalAlertApp.data.Response.Auth.SocialLogin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class AuthRepository @Inject
constructor(private val apiService: ApiService) {


    fun login(
        mobile: String,
        password: String,
        deviceToken:String
    ): Flow<LoginData> = flow {
        emit(apiService.login(mobile, password,deviceToken))
    }.flowOn(Dispatchers.IO)




    fun sociallogin(
        email: String,
        name: String,
        deviceToken:String
    ): Flow<SocialLogin> = flow {
        emit(apiService.socailogin(email, name, deviceToken))
    }.flowOn(Dispatchers.IO)


}