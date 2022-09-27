package com.application.animalAlertApp.View.AddAlerts


import com.application.animalAlertApp.data.Response.GetMyPets

sealed class MyPetsState{
    class Success(val data: GetMyPets) : MyPetsState()
    class Failure(val msg:Throwable) : MyPetsState()
    object Loading  : MyPetsState()
    object Empty : MyPetsState()
}
