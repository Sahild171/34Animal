package com.application.animalAlertApp.data.Response

data class PetTypeResponse(
    val getData: List<GetData>,
    val message: String,
    val status: Int
)