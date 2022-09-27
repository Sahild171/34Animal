package com.application.animalAlertApp.Model

data class AlertModel(
    var name: String,
    var description: String,
    var date: String,
    var profileimage: Int,
    var list: List<ImageModel>
) {
}