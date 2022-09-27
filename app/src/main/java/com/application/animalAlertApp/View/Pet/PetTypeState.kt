package com.application.animalAlertApp.View.Pet


import com.application.animalAlertApp.data.Response.PetTypeResponse

sealed class PetTypeState{
    class Success(val data: PetTypeResponse) : PetTypeState()
    class Failure(val msg:Throwable) : PetTypeState()
    object Loading  : PetTypeState()
    object Empty : PetTypeState()
}
