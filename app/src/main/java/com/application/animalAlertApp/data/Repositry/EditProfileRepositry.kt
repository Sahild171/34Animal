package com.application.animalAlertApp.data.Repositry

import com.application.animalAlertApp.Network.ApiService
import com.application.animalAlertApp.data.Response.GenricResponse
import com.application.animalAlertApp.data.Response.Profile.EditProfileData
import com.application.animalAlertApp.data.Response.Profile.EditProfilePic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import javax.inject.Inject

class EditProfileRepositry @Inject constructor(val apiService: ApiService) {


    fun editprofile(
        name: String,
        phoneNo: String,
        location: String,
        email: String,
        lat: String,
        long: String
    ): Flow<EditProfileData> = flow {
        emit(apiService.editprofile(name, phoneNo, location, email, lat, long))
    }.flowOn(Dispatchers.IO)


    fun editprofilepic(
        image: MultipartBody.Part
    ): Flow<EditProfilePic> = flow {
        emit(apiService.editprofilepic(image))
    }.flowOn(Dispatchers.IO)




}