package com.application.animalAlertApp.data.Response.Questions

data class Docs(
    val __v: Int,
    val _id: String,
    val alertId: String,
    val contactDetail: String,
    val createdAt: String,
    val friendlyToApproach: String,
    val otherComment: String,
    val petImg: List<String>,
    val updatedAt: String,
    val userId: String,
    val whenPetLastSeen: String,
    val wherePetLastSeen: String
)