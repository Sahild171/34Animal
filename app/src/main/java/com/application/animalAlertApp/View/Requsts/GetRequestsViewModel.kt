package com.application.animalAlertApp.View.Requsts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.FriendRequestRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Request.GetfriendRequests
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class GetRequestsViewModel @Inject constructor(val repositry: FriendRequestRepositry) :
    ViewModel() {
    private val _userrequestdata1: MutableLiveData<Resource<GetfriendRequests>> = MutableLiveData()
    val userresuestdata1: LiveData<Resource<GetfriendRequests>> = _userrequestdata1


    private val _acceptreject: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val acceptreject: LiveData<Resource<GenricResponse>> = _acceptreject


    fun getrequests(status: String) = viewModelScope.launch {
        repositry.getrequests(status).onStart {
            _userrequestdata1.value = Resource.Loading
        }.catch { e ->
            _userrequestdata1.value = Resource.Failure(e)
        }.collect { response ->
            _userrequestdata1.value = Resource.Success(response)

        }
    }


    fun acceptorreject(
        status: String,
        friendId: String,
        senderId: String
    ) = viewModelScope.launch {
        repositry.acceptorreject(status, friendId, senderId).onStart {
            _acceptreject.value = Resource.Loading
        }.catch { e ->
            _acceptreject.value = Resource.Failure(e)
        }.collect { data ->
            _acceptreject.value = Resource.Success(data)
        }
    }


}