package com.application.animalAlertApp.View.Requsts

import androidx.lifecycle.ViewModel
import com.application.animalAlertApp.data.Repositry.RequestRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject


@HiltViewModel
class SendRequestViewModel @Inject constructor(val repositry: RequestRepositry) : ViewModel() {

    fun sendrequest(
        userId: String,
        deviceToken:String
    ): Flow<GenricResponse> = repositry.sendrequest(userId,deviceToken)

}