package com.application.animalAlertApp.View.Requsts

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.UserProfileRepositry
import com.application.animalAlertApp.data.Response.OtherUserProfile.GetUserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class UserProfileViewModel @Inject constructor(val repositry: UserProfileRepositry) : ViewModel() {
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
}