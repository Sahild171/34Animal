package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.ForgotResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.VerifyOtpResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ForgotPasswordRepositry @Inject constructor(val apiService: ApiService) {


    fun forgotpassword(
        email: String
    ): Flow<ForgotResponse> = flow {
        emit(apiService.forgotPassword(email))
    }.flowOn(Dispatchers.IO)


    fun verifyotp(
        userId: String, otp: String
    ): Flow<VerifyOtpResponse> = flow {
        emit(apiService.otpVerifyforPass(userId, otp))
    }.flowOn(Dispatchers.IO)


    fun updatepassword(
        id: String, pass: String, conpass: String
    ): Flow<GenricResponse> = flow {
        emit(apiService.updatePass(id, pass, conpass))
    }.flowOn(Dispatchers.IO)
}