package com.application.animalAlertApp.View.Alerts


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.AlertsRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.GetAllAlerts
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlertViewModel @Inject constructor(val repositry: AlertsRepositry) : ViewModel() {
    ///getalerts
    private val _alertData: MutableLiveData<Resource<GetAllAlerts>> = MutableLiveData()
    val alertData: LiveData<Resource<GetAllAlerts>> = _alertData

    private val _reportData: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val reportData: LiveData<Resource<GenricResponse>> = _reportData


    private val _screenData: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val screenData: LiveData<Resource<GenricResponse>> = _screenData

    fun getAllData() = viewModelScope.launch {
        repositry.alerts()
            .onStart {
                _alertData.value = Resource.Loading
            }.catch { e ->
                _alertData.value = Resource.Failure(e)
            }.collect { response ->
                _alertData.value = Resource.Success(response)
            }
    }


    fun getscreen(alertid:String) = viewModelScope.launch {
        repositry.getscreen(alertid)
            .onStart {
                _screenData.value = Resource.Loading
            }.catch { e ->
                _screenData.value = Resource.Failure(e)
            }.collect { response ->
                _screenData.value = Resource.Success(response)
            }
    }

    fun reportAlert(alertid: String, reportBy: String, reason: String) = viewModelScope.launch {
        repositry.reportAlert(alertid, reportBy, reason)
            .onStart {
                _reportData.value = Resource.Loading
            }.catch { e ->
                _reportData.value = Resource.Failure(e)
            }.collect { response ->
                _reportData.value = Resource.Success(response)
            }
    }


    fun deletealert(
        alertid: String
    ) = repositry.deletemyAlerts(alertid)


    fun closealert(
        alertid: String,
        isActive: Boolean
    ) = repositry.closealerts(alertid, isActive)


}