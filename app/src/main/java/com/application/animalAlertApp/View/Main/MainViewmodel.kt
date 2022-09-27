package com.application.animalAlertApp.View.Main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.ShopRepositry
import com.application.animalAlertApp.data.Response.ShopStatusResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewmodel @Inject constructor(val repositry: ShopRepositry) : ViewModel() {
    private val _checkshopdata: MutableLiveData<Resource<ShopStatusResponse>> = MutableLiveData()
    val checkshopdata: LiveData<Resource<ShopStatusResponse>> = _checkshopdata


    fun checkshopdata() = viewModelScope.launch {
        repositry.getstatuscheck().onStart {
            _checkshopdata.value = Resource.Loading
        }.catch { e ->
            _checkshopdata.value = Resource.Failure(e)
        }.collect { response ->
            _checkshopdata.value = Resource.Success(response)
        }
    }
}