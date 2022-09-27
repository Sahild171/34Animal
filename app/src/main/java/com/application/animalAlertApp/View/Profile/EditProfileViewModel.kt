package com.application.animalAlertApp.View.Profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.application.animalAlertApp.Network.Resource
import com.application.animalAlertApp.data.Repositry.EditProfileRepositry
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Profile.EditProfileData
import com.application.animalAlertApp.data.Response.Profile.EditProfilePic
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import javax.inject.Inject


@HiltViewModel
class EditProfileViewModel @Inject constructor(val repositry: EditProfileRepositry) : ViewModel() {
    private val _editpicdata: MutableLiveData<Resource<EditProfilePic>> =
        MutableLiveData()
    val editpicdata: LiveData<Resource<EditProfilePic>> = _editpicdata



    fun editprofile(
        name: String,
        phoneNo: String,
        location: String,
        email: String,
        lat: String,
        long: String
    ): Flow<EditProfileData> = repositry.editprofile(name, phoneNo, location, email,lat, long)


    fun editprofilepic(
        image: MultipartBody.Part
    ): Flow<EditProfilePic> = repositry.editprofilepic(image)


    fun editprofilepic1(image: MultipartBody.Part) = viewModelScope.launch {
        repositry.editprofilepic(image).onStart {
            _editpicdata.value = Resource.Loading
        }.catch { e ->
            _editpicdata.value = Resource.Failure(e)
        }.collect { response ->
            _editpicdata.value = Resource.Success(response)
        }
    }





}