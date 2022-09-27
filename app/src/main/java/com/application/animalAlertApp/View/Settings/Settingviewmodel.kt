package com.application.animalAlertApp.View.Settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.ChangePasswordRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.OtherUserProfile.GetUserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class Settingviewmodel @Inject constructor(val repositry:ChangePasswordRepositry):ViewModel() {



    private val _changepassworddata: MutableLiveData<Resource<GenricResponse>> = MutableLiveData()
    val changepassworddata: LiveData<Resource<GenricResponse>> = _changepassworddata


    private val _editlocationvisibility: MutableLiveData<Resource<GenricResponse>> =
        MutableLiveData()
    val editlocationvisibility: LiveData<Resource<GenricResponse>> = _editlocationvisibility


    private val _userprofiledata: MutableLiveData<Resource<GetUserProfile>> = MutableLiveData()
    val userprofiledata: LiveData<Resource<GetUserProfile>> = _userprofiledata


    fun getuserprofile(userid:String) = viewModelScope.launch {
        repositry.getuserprofile(userid).onStart {
            _userprofiledata.value = Resource.Loading
        }.catch { e ->
            _userprofiledata.value = Resource.Failure(e)
        }.collect { response ->
            _userprofiledata.value = Resource.Success(response)
        }
    }


    fun changepassword(oldpass:String,newpass:String) = viewModelScope.launch {
        repositry.changepassword(oldpass,newpass).onStart {
            _changepassworddata.value = Resource.Loading
        }.catch { e ->
            _changepassworddata.value = Resource.Failure(e)
        }.collect { response ->
            _changepassworddata.value = Resource.Success(response)

        }
    }

    fun editlocationvisibility(locationVisibility: Boolean) = viewModelScope.launch {
        repositry.editlocationvisibility(locationVisibility).onStart {
            _editlocationvisibility.value = Resource.Loading
        }.catch { e ->
            _editlocationvisibility.value = Resource.Failure(e)
        }.collect { response ->
            _editlocationvisibility.value = Resource.Success(response)
        }
    }


}