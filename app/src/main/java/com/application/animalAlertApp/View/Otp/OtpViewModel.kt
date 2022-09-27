package com.application.animalAlertApp.View.Otp

import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.data.Repositry.VeriftOtpRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class OtpViewModel @Inject
constructor(val repositry: VeriftOtpRepositry):ViewModel() {

    fun verifyotp(
        id: String,
        otp: String
    ) = repositry.verifyotp(id,otp)


    fun resendotp(
        id: String
    ) = repositry.resendotp(id)


    fun changeno(
        user_id:String,
        phoneNo:String
    )=repositry.changeno(user_id, phoneNo)


}