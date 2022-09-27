package com.application.animalAlertApp.data.Response

data class GetMyPets(
    val message:String,
    val findPet: List<MessageXX>,
    val status: Int
)