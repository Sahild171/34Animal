package com.application.animalAlertApp.data.Response.Profile

data class SearchUserResponse(
    val message: String,
    val searchUser: List<SearchUser>,
    val status: Int
)