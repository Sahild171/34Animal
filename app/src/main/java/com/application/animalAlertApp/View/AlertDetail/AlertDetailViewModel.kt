package com.application.animalAlertApp.View.AlertDetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.data.Repositry.AlertDetailRepositry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AlertDetailViewModel @Inject constructor(val repositry: AlertDetailRepositry) : ViewModel() {


    ///getalerts
    private val _alertData: MutableStateFlow<AlertDetailState> =
        MutableStateFlow(AlertDetailState.Empty)
    val alertData: StateFlow<AlertDetailState> = _alertData


    fun getAlertdetail(alertid: String) = viewModelScope.launch {
        repositry.getAlertDetail(alertid)
            .onStart {
                _alertData.value = AlertDetailState.Loading
            }.catch { e ->
                _alertData.value = AlertDetailState.Failure(e)
            }.collect { response ->
                _alertData.value = AlertDetailState.Success(response)
            }
    }




}