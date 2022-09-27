package com.application.animalAlertApp.View.BusinessProfile.EditBussProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.EditServiceRepositry
import com.application.animalAlertApp.data.Response.BusinessProfile.EditServiceResponse
import com.application.animalAlertApp.data.Response.Service.ServiceResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class EditServiceViewmodel @Inject constructor(val repositry: EditServiceRepositry) : ViewModel() {


    private val _serivcedata: MutableLiveData<Resource<ServiceResponse>> = MutableLiveData()
    val serivcedata: LiveData<Resource<ServiceResponse>> = _serivcedata

    private val _editserivcedata: MutableLiveData<Resource<EditServiceResponse>> = MutableLiveData()
    val editserivcedata: LiveData<Resource<EditServiceResponse>> = _editserivcedata

    fun getservices() = viewModelScope.launch {
        repositry.getserivces().onStart {
            _serivcedata.value = Resource.Loading
        }.catch { e ->
            _serivcedata.value = Resource.Failure(e)
        }.collect { data ->
            _serivcedata.value = Resource.Success(data)
        }
    }

    fun editservices(
        serviceId: String,
        service: String,
        priceperiod: String,
        price: String,
        description: String
    ) = viewModelScope.launch {
        repositry.editservices(serviceId, service, priceperiod, price, description).onStart {
            _editserivcedata.value = Resource.Loading
        }.catch { e ->
            _editserivcedata.value = Resource.Failure(e)
        }.collect { data ->
            _editserivcedata.value = Resource.Success(data)
        }
    }


}