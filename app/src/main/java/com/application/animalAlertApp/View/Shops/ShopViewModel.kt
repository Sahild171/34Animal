package com.application.animalAlertApp.View.Shops

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.ShopRepositry
import com.application.animalAlertApp.data.Response.BusinessProfile.BusinessProfileResponse
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Service.ServiceResponse
import com.application.animalAlertApp.data.Response.Shop.AddShopResponse
import com.application.animalAlertApp.data.Response.Shop.GetAllShops
import com.application.animalAlertApp.data.Response.ShopStatusResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
class ShopViewModel @Inject constructor(val repositry: ShopRepositry) : ViewModel() {

    private val _shopdata: MutableLiveData<Resource<AddShopResponse>> = MutableLiveData()
    val shopdata: LiveData<Resource<AddShopResponse>> = _shopdata


    private val _shoppicdata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val shoppicdata: LiveData<Resource<GenricResponse>> = _shoppicdata


    private val _allshopdata: MutableLiveData<Resource<GetAllShops>> = MutableLiveData()
    val allshopdata: LiveData<Resource<GetAllShops>> = _allshopdata


    private val _serivcedata: MutableLiveData<Resource<ServiceResponse>> = MutableLiveData()
    val serivcedata: LiveData<Resource<ServiceResponse>> = _serivcedata

    private val _addserivcedata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val addserivcedata: LiveData<Resource<GenricResponse>> = _addserivcedata

    private val _checkshopstatus: MutableLiveData<Resource<ShopStatusResponse>> = MutableLiveData()
    val checkshopstatus: LiveData<Resource<ShopStatusResponse>> = _checkshopstatus


    private val _businessdata: MutableLiveData<Resource<BusinessProfileResponse>> =
        MutableLiveData()
    val businessdata: LiveData<Resource<BusinessProfileResponse>> = _businessdata


    fun addshop(
        businessname: String,
        businessdescription: String,
        mobileno: String,
        location: String
    ) = viewModelScope.launch {
        repositry.addshop(
            businessname,
            businessdescription,
            mobileno,
            location
        ).onStart {
            _shopdata.value = Resource.Loading
        }.catch { e ->
            _shopdata.value = Resource.Failure(e)
        }.collect { response ->
            _shopdata.value = Resource.Success(response)

        }
    }


    fun addportfolio(
        uploadLogo: MultipartBody.Part,
        coverPhoto: MultipartBody.Part,
        portfolioLogo: List<MultipartBody.Part>
    ) = viewModelScope.launch {
        repositry.addportfolio(uploadLogo, coverPhoto, portfolioLogo).onStart {
            _shoppicdata.value = Resource.Loading
        }.catch { e ->
            _shoppicdata.value = Resource.Failure(e)
        }.collect { response ->
            _shoppicdata.value = Resource.Success(response)
        }
    }

    fun getAllshops() = viewModelScope.launch {
        repositry.getAllShops().onStart {
            _allshopdata.value = Resource.Loading
        }.catch { e ->
            _allshopdata.value = Resource.Failure(e)
        }.collect { response ->
            _allshopdata.value = Resource.Success(response)
        }
    }


    fun getservices() = viewModelScope.launch {
        repositry.getserivces().onStart {
            _serivcedata.value = Resource.Loading
        }.catch { e ->
            _serivcedata.value = Resource.Failure(e)
        }.collect { data ->
            _serivcedata.value = Resource.Success(data)
        }
    }


    fun addshopservices(services: String) = viewModelScope.launch {
        repositry.addshopserivces(services).onStart {
            _addserivcedata.value = Resource.Loading
        }.catch { e ->
            _addserivcedata.value = Resource.Failure(e)
        }.collect { data ->
            _addserivcedata.value = Resource.Success(data)
        }
    }

    fun checkShopStatus() = viewModelScope.launch {
        repositry.getstatuscheck().onStart {
            _checkshopstatus.value = Resource.Loading
        }.catch { e ->
            _checkshopstatus.value = Resource.Failure(e)
        }.collect { data ->
            _checkshopstatus.value = Resource.Success(data)
        }
    }


    fun getbusinessdata(userId: String) = viewModelScope.launch {
        repositry.getbusinessprofile(userId).onStart {
            _businessdata.value = Resource.Loading
        }.catch { e ->
            _businessdata.value = Resource.Failure(e)
        }.collect { response ->
            _businessdata.value = Resource.Success(response)
        }
    }



}