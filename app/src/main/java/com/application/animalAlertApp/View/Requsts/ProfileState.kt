package com.application.animalAlertApp.View.Requsts


import com.application.animalAlertApp.data.Response.OtherUserProfile.GetUserProfile


sealed class ProfileState {
    class Success(val data: GetUserProfile) : ProfileState()
    class Failure(val msg: Throwable) : ProfileState()
    object Loading : ProfileState()
    object Empty : ProfileState()
}
