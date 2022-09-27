package com.application.animalAlertApp.View.ForgotPassword

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.ForgotPasswordRepositry
import com.application.animalAlertApp.data.Response.ForgotResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.VerifyOtpResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class ForgotviewModel @Inject constructor(val respositry:ForgotPasswordRepositry):ViewModel() {

    private val _forgotreponse: MutableLiveData<Resource<ForgotResponse>> = MutableLiveData()
    val forgotreponse: LiveData<Resource<ForgotResponse>> = _forgotreponse

    private val _verifyreponse: MutableLiveData<Resource<VerifyOtpResponse>> = MutableLiveData()
    val verifyreponse: LiveData<Resource<VerifyOtpResponse>> = _verifyreponse

    private val _updatepassword: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val updatepassword: LiveData<Resource<GenricResponse>> = _updatepassword

    fun forgotpassword(email: String) = viewModelScope.launch {
        respositry.forgotpassword(email).onStart {
            _forgotreponse.value = Resource.Loading
        }.catch { e ->
            _forgotreponse.value = Resource.Failure(e)
        }.collect { response ->
            _forgotreponse.value = Resource.Success(response)
        }
    }

    fun verifyotp(userid: String,otp:String) = viewModelScope.launch {
        respositry.verifyotp(userid,otp).onStart {
            _verifyreponse.value = Resource.Loading
        }.catch { e ->
            _verifyreponse.value = Resource.Failure(e)
        }.collect { response ->
            _verifyreponse.value = Resource.Success(response)
        }
    }


    fun updatepassword(id: String,pass:String,conpass:String) = viewModelScope.launch {
        respositry.updatepassword(id,pass,conpass).onStart {
            _updatepassword.value = Resource.Loading
        }.catch { e ->
            _updatepassword.value = Resource.Failure(e)
        }.collect { response ->
            _updatepassword.value = Resource.Success(response)
        }
    }



}