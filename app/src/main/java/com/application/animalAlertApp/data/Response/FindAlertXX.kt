package com.application.animalAlertApp.data.Response

data class FindAlertXX(
    val _id: String,
    val addTitle: String,
    val description: String,
    val isActive: Boolean,
    val petColor: String,
    val petId: String,
    val petDescription: String,
    val petImages: List<String>,
    val petName: String,
    val priority: String,
    val petBreed:String
)