package com.application.animalAlertApp.View.Notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.NotificationRepositry
import com.application.animalAlertApp.data.Response.NotificationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class NotificationViewModel @Inject constructor(val repositry:NotificationRepositry):ViewModel() {
    private val _notificationdata: MutableLiveData<Resource<NotificationResponse>> = MutableLiveData()
    val notificationdata: LiveData<Resource<NotificationResponse>> = _notificationdata


    fun getallnotification()= viewModelScope.launch {
        repositry.getNotifications().onStart {
            _notificationdata.value = Resource.Loading
        }.catch { e ->
            _notificationdata.value = Resource.Failure(e)
        }.collect { response ->
            _notificationdata.value = Resource.Success(response)
        }
    }




}