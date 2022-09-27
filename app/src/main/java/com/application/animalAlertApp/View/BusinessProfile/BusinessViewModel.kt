package com.application.animalAlertApp.View.BusinessProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.BusinessRepositry
import com.application.animalAlertApp.data.Response.BusinessProfile.AddPortfolioresponse
import com.application.animalAlertApp.data.Response.BusinessProfile.BusinessProfileResponse
import com.application.animalAlertApp.data.Response.BusinessProfile.EditcoverResopnse
import com.application.animalAlertApp.data.Response.GenricResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
class BusinessViewModel @Inject constructor(private val repositry: BusinessRepositry) :
    ViewModel() {
    private val _businessdata: MutableLiveData<Resource<BusinessProfileResponse>> =
        MutableLiveData()
    val businessdata: LiveData<Resource<BusinessProfileResponse>> = _businessdata

    private val _editcover: MutableLiveData<Resource<EditcoverResopnse>> = MutableLiveData()
    val editcover: LiveData<Resource<EditcoverResopnse>> = _editcover

    private val _editlogo: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val editlogo: LiveData<Resource<GenricResponse>> = _editlogo


    private val _editportfolio: MutableLiveData<Resource<AddPortfolioresponse>> = MutableLiveData()
    val editportfolio: LiveData<Resource<AddPortfolioresponse>> = _editportfolio


    private val _deleteservice: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val deleteservice: LiveData<Resource<GenricResponse>> = _deleteservice


    private val _deleteportfolio: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val deleteportfolio: LiveData<Resource<GenricResponse>> = _deleteportfolio

    fun getbusinessdata(userId: String) = viewModelScope.launch {
        repositry.getbusinessprofile(userId).onStart {
            _businessdata.value = Resource.Loading
        }.catch { e ->
            _businessdata.value = Resource.Failure(e)
        }.collect { response ->
            _businessdata.value = Resource.Success(response)
        }
    }

    fun editcoverphoto(coverPhoto: MultipartBody.Part) = viewModelScope.launch {
        repositry.editcoverphoto(coverPhoto).onStart {
            _editcover.value = Resource.Loading
        }.catch { e ->
            _editcover.value = Resource.Failure(e)
        }.collect { response ->
            _editcover.value = Resource.Success(response)
        }
    }

    fun editshoplogo(uploadLogo: MultipartBody.Part) = viewModelScope.launch {
        repositry.editshoplogo(uploadLogo).onStart {
            _editlogo.value = Resource.Loading
        }.catch { e ->
            _editlogo.value = Resource.Failure(e)
        }.collect { response ->
            _editlogo.value = Resource.Success(response)
        }
    }


    fun editportfoio(portfolioLogo: MultipartBody.Part) = viewModelScope.launch {
        repositry.editportfolio(portfolioLogo).onStart {
            _editportfolio.value = Resource.Loading
        }.catch { e ->
            _editportfolio.value = Resource.Failure(e)
        }.collect { response ->
            _editportfolio.value = Resource.Success(response)
        }
    }

    fun deleteservice(serviceId: String) = viewModelScope.launch {
        repositry.deleteservice(serviceId).onStart {
            _deleteservice.value = Resource.Loading
        }.catch { e ->
            _deleteservice.value = Resource.Failure(e)
        }.collect { response ->
            _deleteservice.value = Resource.Success(response)
        }
    }

    fun deleteportfoliophoto(portfolioLogo: String) = viewModelScope.launch {
        repositry.deleteportfolio(portfolioLogo).onStart {
            _deleteportfolio.value = Resource.Loading
        }.catch { e ->
            _deleteportfolio.value = Resource.Failure(e)
        }.collect { response ->
            _deleteportfolio.value = Resource.Success(response)
        }
    }



}