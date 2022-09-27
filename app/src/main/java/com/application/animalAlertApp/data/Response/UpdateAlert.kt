package com.application.animalAlertApp.data.Response

import java.io.Serializable

data class UpdateAlert(
    val Status: String,
    val __v: Int,
    val _id: String,
    val addTitle: String,
    val createdAt: String,
    val description: String,
    val isActive: Boolean,
    val petId: String,
    val priority: String,
    val selectPet: String,
    val updatedAt: String,
    val userId: String
):Serializable