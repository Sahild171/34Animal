package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.ChangePhoneResponse
import com.application.animalAlertApp.data.Response.OtpResponse
import com.application.animalAlertApp.data.Response.ResendOtp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class VeriftOtpRepositry @Inject constructor(private val apiService: ApiService)  {

    fun verifyotp(
        id: String,
        otp: String
    ): Flow<OtpResponse> = flow {
        emit(apiService.verifyOtp(id,otp))
    }.flowOn(Dispatchers.IO)


    fun resendotp(
        id: String
    ): Flow<ResendOtp> = flow {
        emit(apiService.resendotp(id))
    }.flowOn(Dispatchers.IO)


    fun changeno(
        user_id: String,
        phoneNo:String
    ): Flow<ChangePhoneResponse> = flow {
        emit(apiService.changePhoneno(user_id, phoneNo))
    }.flowOn(Dispatchers.IO)

}