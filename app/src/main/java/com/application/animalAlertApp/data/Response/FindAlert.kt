package com.application.animalAlertApp.data.Response

data class FindAlert(
    val _id: String,
    val addTitle: String,
    val description: String,
    val impressions: Int,
    val isActive: Boolean,
    val petBread: String,
    val petColor: String,
    val petId: String,
    val petImages: List<PetImageX>,
    val petName: String,
    val priority: String
)