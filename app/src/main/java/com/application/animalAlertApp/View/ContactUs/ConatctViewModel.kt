package com.application.animalAlertApp.View.ContactUs

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.ContactRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ConatctViewModel @Inject constructor(val repositry: ContactRepositry) : ViewModel() {

    private val _contactdata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val contactdata: LiveData<Resource<GenricResponse>> = _contactdata


    fun contactus(name: String, email: String, message: String) = viewModelScope.launch {
        repositry.contactus(name, email, message).onStart {
            _contactdata.value = Resource.Loading
        }.catch { e ->
            _contactdata.value = Resource.Failure(e)
        }.collect { response ->
            _contactdata.value = Resource.Success(response)

        }
    }
}